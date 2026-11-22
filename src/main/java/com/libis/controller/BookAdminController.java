package com.libis.controller;

import com.libis.exception.ValidationException;
import com.libis.model.Book;
import com.libis.service.BookService;
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
 *   GET  /librarian/books            list / search (?q=)
 *   GET  /librarian/books/new        add form
 *   POST /librarian/books/create     insert
 *   GET  /librarian/books/edit?id=   edit form, pre-filled
 *   POST /librarian/books/update     update
 *   GET  /librarian/books/delete?id= confirmation page
 *   POST /librarian/books/delete     delete (refused while on loan)
 */
@WebServlet(name = "BookAdminController", urlPatterns = {"/librarian/books", "/librarian/books/*"})
public class BookAdminController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String[] FIELDS =
            {"id", "isbn", "title", "author", "genre", "publishedYear", "description"};

    private static final String LIST_VIEW = "/WEB-INF/views/librarian/books.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/librarian/book-form.jsp";
    private static final String DELETE_VIEW = "/WEB-INF/views/librarian/book-delete.jsp";

    private transient BookService bookService;

    @Override
    public void init() {
        bookService = new BookService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (action(request)) {
            case "" -> {
                String q = ControllerSupport.trimToEmpty(request.getParameter("q"));
                request.setAttribute("books", bookService.list(q));
                request.setAttribute("query", q);
                forward(request, response, LIST_VIEW);
            }
            case "new" -> showForm(request, response, new LinkedHashMap<>(), false);
            case "edit" -> {
                Optional<Book> b = bookService.findById(ControllerSupport.intParam(request, "id"));
                if (b.isEmpty()) {
                    notFound(request, response);
                    return;
                }
                showForm(request, response, toForm(b.get()), true);
            }
            case "delete" -> {
                Optional<Book> b = bookService.findById(ControllerSupport.intParam(request, "id"));
                if (b.isEmpty()) {
                    notFound(request, response);
                    return;
                }
                request.setAttribute("book", b.get());
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
                Book book = fromRequest(request);
                try {
                    bookService.create(book, request.getParameter("publishedYear"));
                    ControllerSupport.flash(request, "\"" + book.getTitle() + "\" has been added to the catalogue.");
                    redirectToList(request, response);
                } catch (ValidationException e) {
                    request.setAttribute("errors", e.getErrors());
                    showForm(request, response, ControllerSupport.formFromRequest(request, FIELDS), false);
                }
            }
            case "update" -> {
                Book book = fromRequest(request);
                try {
                    bookService.update(book, request.getParameter("publishedYear"));
                    ControllerSupport.flash(request, "\"" + book.getTitle() + "\" has been updated.");
                    redirectToList(request, response);
                } catch (ValidationException e) {
                    request.setAttribute("errors", e.getErrors());
                    showForm(request, response, ControllerSupport.formFromRequest(request, FIELDS), true);
                }
            }
            case "delete" -> {
                try {
                    Book deleted = bookService.delete(ControllerSupport.intParam(request, "id"));
                    ControllerSupport.flash(request, "\"" + deleted.getTitle() + "\" has been deleted.");
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

    /** Builds a Book from the form. The year is validated (and set) by BookService from the raw text. */
    private static Book fromRequest(HttpServletRequest request) {
        Book b = new Book();
        b.setId(Math.max(ControllerSupport.intParam(request, "id"), 0));
        b.setIsbn(request.getParameter("isbn"));
        b.setTitle(request.getParameter("title"));
        b.setAuthor(request.getParameter("author"));
        b.setGenre(request.getParameter("genre"));
        b.setDescription(request.getParameter("description"));
        return b;
    }

    private static Map<String, String> toForm(Book b) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("id", String.valueOf(b.getId()));
        form.put("isbn", b.getIsbn());
        form.put("title", b.getTitle());
        form.put("author", b.getAuthor());
        form.put("genre", b.getGenre() == null ? "" : b.getGenre());
        form.put("publishedYear", b.getPublishedYear() == null ? "" : String.valueOf(b.getPublishedYear()));
        form.put("description", b.getDescription() == null ? "" : b.getDescription());
        return form;
    }

    private static void showForm(HttpServletRequest request, HttpServletResponse response,
                                 Map<String, String> form, boolean editing)
            throws ServletException, IOException {
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
        response.sendRedirect(request.getContextPath() + "/librarian/books");
    }

    private static void notFound(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ControllerSupport.flashError(request, "That book could not be found.");
        redirectToList(request, response);
    }
}
