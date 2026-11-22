package com.libis.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * "" is the context root (http://localhost:8080/LIBIS_LIBRARY/) -> catalogue.
 * "/librarian" -> the librarian's book list.
 */
@WebServlet(name = "HomeController", urlPatterns = {"", "/librarian"})
public class HomeController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String target = "/librarian".equals(request.getServletPath()) ? "/librarian/books" : "/catalogue";
        response.sendRedirect(request.getContextPath() + target);
    }
}
