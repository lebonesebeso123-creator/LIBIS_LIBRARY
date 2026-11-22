package com.libis.controller;

import com.libis.data.LibraryData;
import com.libis.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CatalogueController", urlPatterns = {"/catalogue"})
public class CatalogueController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String query = request.getParameter("q");

        List<Book> books = LibraryData.searchBooks(query);

        request.setAttribute("books", books);
        request.setAttribute("query", query == null ? "" : query.trim());
        request.setAttribute("totalBooks", LibraryData.getBooks().size());

        // welcome.jsp lives in WEB-INF, so it can only be reached through this forward
        request.getRequestDispatcher("/WEB-INF/welcome.jsp").forward(request, response);
    }
}
