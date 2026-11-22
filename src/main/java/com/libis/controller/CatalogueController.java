package com.libis.controller;

import com.libis.dao.BookDao;
import com.libis.dao.BookDaoImpl;
import com.libis.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Same job as in CA2: read the request, get books, forward to the view.
 * Only the source changed: BookDao (MySQL) instead of the ArrayList,
 * and the search is now a parameterised SQL query.
 */
@WebServlet(name = "CatalogueController", urlPatterns = {"/catalogue"})
public class CatalogueController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient BookDao bookDao;

    @Override
    public void init() {
        bookDao = new BookDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = ControllerSupport.trimToEmpty(request.getParameter("q"));
        List<Book> books = query.isEmpty() ? bookDao.findAll() : bookDao.search(query);

        request.setAttribute("books", books);
        request.setAttribute("query", query);
        request.getRequestDispatcher("/WEB-INF/views/welcome.jsp").forward(request, response);
    }

    /**
     * Only reached by an internal forward from LoanController, when a borrow is rejected
     * and the catalogue is re-displayed with the reason.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
