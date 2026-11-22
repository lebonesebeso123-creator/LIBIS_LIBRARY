package com.libis.controller;

import com.libis.exception.LoanException;
import com.libis.model.Book;
import com.libis.model.Loan;
import com.libis.model.Member;
import com.libis.service.LoanService;
import com.libis.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * GET  /loans         My Loans (current loans + borrowing history)
 * POST /loans/borrow  borrow a book   (bookId)
 * POST /loans/return  return a book   (bookId)
 *
 * The controller never decides: LoanService does, and throws LoanException with the reason.
 * The member is always identified through request.getRemoteUser().
 */
@WebServlet(name = "LoanController", urlPatterns = {"/loans", "/loans/borrow", "/loans/return"})
public class LoanController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient LoanService loanService;
    private transient MemberService memberService;

    @Override
    public void init() {
        loanService = new LoanService();
        memberService = new MemberService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/loans".equals(request.getServletPath())) {
            // borrow/return are POST only
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Optional<Member> member = ControllerSupport.currentMember(request, memberService);
        if (member.isEmpty()) {
            signedInMemberMissing(request, response);
            return;
        }
        showMyLoans(request, response, member.get());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Member> current = ControllerSupport.currentMember(request, memberService);
        if (current.isEmpty()) {
            signedInMemberMissing(request, response);
            return;
        }
        Member member = current.get();
        int bookId = ControllerSupport.intParam(request, "bookId");

        switch (request.getServletPath()) {
            case "/loans/borrow" -> {
                try {
                    Book book = loanService.borrow(member, bookId);
                    ControllerSupport.flashKey(request, "flash.borrowed", book.getTitle());
                    response.sendRedirect(request.getContextPath() + "/loans");
                } catch (LoanException e) {
                    // Re-display the catalogue with the specific reason
                    request.setAttribute("loanError", e.getMessage());
                    request.getRequestDispatcher("/catalogue").forward(request, response);
                }
            }
            case "/loans/return" -> {
                try {
                    Loan loan = loanService.returnBook(member, bookId);
                    ControllerSupport.flashKey(request, "flash.returned", loan.getBookTitle());
                    response.sendRedirect(request.getContextPath() + "/loans");
                } catch (LoanException e) {
                    request.setAttribute("loanError", e.getMessage());
                    showMyLoans(request, response, member);
                }
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showMyLoans(HttpServletRequest request, HttpServletResponse response, Member member)
            throws ServletException, IOException {
        List<Loan> current = loanService.currentLoans(member);
        request.setAttribute("member", member);
        request.setAttribute("currentLoans", current);
        request.setAttribute("history", loanService.history(member));
        request.setAttribute("loanCount", current.size());
        request.setAttribute("maxLoans", LoanService.MAX_LOANS);
        request.getRequestDispatcher("/WEB-INF/views/my-loans.jsp").forward(request, response);
    }

    /** The account was deleted while signed in: end the session and go back to the login page. */
    private void signedInMemberMissing(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.logout();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/catalogue");
    }
}
