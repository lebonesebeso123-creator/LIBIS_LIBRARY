package com.libis.controller;

import com.libis.exception.ValidationException;
import com.libis.model.Member;
import com.libis.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Public self-registration (not covered by any security-constraint).
 *   GET  /register  sign-up form
 *   POST /register  create a MEMBER account, then back to the login page
 * New accounts are always members; only a librarian can create another librarian.
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/register.jsp";
    private static final String[] FIELDS = {"fullName", "email", "username"};

    private transient MemberService memberService;

    @Override
    public void init() {
        memberService = new MemberService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getRemoteUser() != null) {
            // already signed in: nothing to register
            response.sendRedirect(request.getContextPath() + "/catalogue");
            return;
        }
        request.setAttribute("form", new LinkedHashMap<String, String>());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Member member = new Member();
        member.setFullName(request.getParameter("fullName"));
        member.setEmail(request.getParameter("email"));
        member.setUsername(request.getParameter("username"));
        member.setPassword(request.getParameter("password"));

        try {
            Member created = memberService.register(member, request.getParameter("confirmPassword"));
            // Post/Redirect/Get: the login page shows "account created" (and the member number)
            String url = request.getContextPath() + "/catalogue?registered="
                    + java.net.URLEncoder.encode(created.getMemberNumber(), java.nio.charset.StandardCharsets.UTF_8);
            response.sendRedirect(url);
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("form", ControllerSupport.formFromRequest(request, FIELDS));
            request.getRequestDispatcher(VIEW).forward(request, response);
        }
    }
}
