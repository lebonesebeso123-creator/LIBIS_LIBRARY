package com.libis.controller;

import com.libis.exception.ValidationException;
import com.libis.model.Member;
import com.libis.model.Role;
import com.libis.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Librarian-only (see web.xml):
 *   GET  /librarian/members            list / search (?q=)
 *   GET  /librarian/members/new        add form
 *   POST /librarian/members/create     insert
 *   GET  /librarian/members/edit?id=   edit form, pre-filled
 *   POST /librarian/members/update     update (blank password keeps the old one)
 *   GET  /librarian/members/delete?id= confirmation page
 *   POST /librarian/members/delete     delete (cascade removes loan history)
 */
@WebServlet(name = "MemberAdminController", urlPatterns = {"/librarian/members", "/librarian/members/*"})
public class MemberAdminController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String[] FIELDS =
            {"id", "memberNumber", "fullName", "email", "username", "password", "role"};

    private static final String LIST_VIEW = "/WEB-INF/views/librarian/members.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/librarian/member-form.jsp";
    private static final String DELETE_VIEW = "/WEB-INF/views/librarian/member-delete.jsp";

    private transient MemberService memberService;

    @Override
    public void init() {
        memberService = new MemberService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = action(request);
        switch (action) {
            case "" -> {
                String q = ControllerSupport.trimToEmpty(request.getParameter("q"));
                request.setAttribute("members", memberService.list(q));
                request.setAttribute("query", q);
                forward(request, response, LIST_VIEW);
            }
            case "new" -> {
                Map<String, String> form = new LinkedHashMap<>();
                form.put("role", Role.MEMBER.value());
                showForm(request, response, form, false);
            }
            case "edit" -> {
                Optional<Member> m = memberService.findById(ControllerSupport.intParam(request, "id"));
                if (m.isEmpty()) {
                    notFound(request, response);
                    return;
                }
                showForm(request, response, toForm(m.get()), true);
            }
            case "delete" -> {
                Optional<Member> m = memberService.findById(ControllerSupport.intParam(request, "id"));
                if (m.isEmpty()) {
                    notFound(request, response);
                    return;
                }
                request.setAttribute("member", m.get());
                request.setAttribute("isSelf", m.get().getUsername().equals(request.getRemoteUser()));
                forward(request, response, DELETE_VIEW);
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (action(request)) {
            case "create" -> {
                Member member = fromRequest(request);
                try {
                    memberService.create(member);
                    ControllerSupport.flash(request, "Member " + member.getMemberNumber()
                            + " (" + member.getFullName() + ") has been created.");
                    redirectToList(request, response);
                } catch (ValidationException e) {
                    request.setAttribute("errors", e.getErrors());
                    showForm(request, response, ControllerSupport.formFromRequest(request, FIELDS), false);
                }
            }
            case "update" -> {
                Member member = fromRequest(request);
                try {
                    memberService.update(member, request.getRemoteUser());
                    ControllerSupport.flash(request, "Member " + member.getMemberNumber()
                            + " (" + member.getFullName() + ") has been updated.");
                    redirectToList(request, response);
                } catch (ValidationException e) {
                    request.setAttribute("errors", e.getErrors());
                    showForm(request, response, ControllerSupport.formFromRequest(request, FIELDS), true);
                }
            }
            case "delete" -> {
                int id = ControllerSupport.intParam(request, "id");
                try {
                    Member deleted = memberService.delete(id, request.getRemoteUser());
                    ControllerSupport.flash(request, "Member " + deleted.getMemberNumber()
                            + " (" + deleted.getFullName() + ") and their borrowing history have been deleted.");
                } catch (ValidationException e) {
                    ControllerSupport.flashError(request, e.getMessage());
                }
                redirectToList(request, response);
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ------------------------------------------------------------------ helpers

    private static String action(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null || path.equals("/") ? "" : path.substring(1);
    }

    private static Member fromRequest(HttpServletRequest request) {
        Member m = new Member();
        int id = ControllerSupport.intParam(request, "id");
        m.setId(Math.max(id, 0));
        m.setMemberNumber(request.getParameter("memberNumber"));
        m.setFullName(request.getParameter("fullName"));
        m.setEmail(request.getParameter("email"));
        m.setUsername(request.getParameter("username"));
        m.setPassword(request.getParameter("password"));
        m.setRole(Role.fromValue(request.getParameter("role")));
        return m;
    }

    private static Map<String, String> toForm(Member m) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("id", String.valueOf(m.getId()));
        form.put("memberNumber", m.getMemberNumber());
        form.put("fullName", m.getFullName());
        form.put("email", m.getEmail());
        form.put("username", m.getUsername());
        form.put("password", "");                 // never sent back to the browser
        form.put("role", m.getRole().value());
        return form;
    }

    private static void showForm(HttpServletRequest request, HttpServletResponse response,
                                 Map<String, String> form, boolean editing)
            throws ServletException, IOException {
        form.put("password", "");                 // never re-display a password
        request.setAttribute("form", form);
        request.setAttribute("editing", editing);
        forward(request, response, FORM_VIEW);
    }

    private static void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }

    private static void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/librarian/members");
    }

    private static void notFound(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ControllerSupport.flashError(request, "That member could not be found.");
        redirectToList(request, response);
    }
}
