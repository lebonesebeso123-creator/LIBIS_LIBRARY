package com.libis.controller;

import com.libis.data.LibraryData;
import com.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    /** Session attribute name used by the filter and the JSPs. */
    public static final String SESSION_MEMBER = "loggedInMember";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Already signed in? Go straight to the catalogue.
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_MEMBER) != null) {
            response.sendRedirect(request.getContextPath() + "/catalogue");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Member member = LibraryData.findMember(email, password);

        if (member != null) {
            // New session on login (prevents session fixation)
            HttpSession old = request.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_MEMBER, member);
            response.sendRedirect(request.getContextPath() + "/catalogue");
        } else {
            request.setAttribute("error", "Invalid email or password. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
