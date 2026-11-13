package com.libis.filter;

import com.libis.controller.LoginController;
import com.libis.model.Member;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String path = uri.substring(context.length());

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Member member = null;
        if (session != null) {
            member = (Member) session.getAttribute(LoginController.SESSION_MEMBER);
        }

        if (member == null) {
            res.sendRedirect(context + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        if (path.equals("/login")
                || path.equals("/login.jsp")
                || path.equals("/")
                || path.equals("/index.jsp")) {
            return true;
        }
        if (path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/assets/")) {
            return true;
        }
        return false;
    }
}