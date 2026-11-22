package com.libis.service;

import com.libis.dao.BookDao;
import com.libis.dao.BookDaoImpl;
import com.libis.dao.LoanDao;
import com.libis.dao.LoanDaoImpl;
import com.libis.exception.ValidationException;
import com.libis.model.Book;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Validation, duplicate checks and rules for book management. */
public class BookService {

    private final BookDao bookDao;
    private final LoanDao loanDao;

    public BookService() {
        this(new BookDaoImpl(), new LoanDaoImpl());
    }

    public BookService(BookDao bookDao, LoanDao loanDao) {
        this.bookDao = bookDao;
        this.loanDao = loanDao;
    }

    public List<Book> list(String term) {
        return term == null || term.isBlank() ? bookDao.findAll() : bookDao.search(term);
    }

    public Optional<Book> findById(int id) {
        return bookDao.findById(id);
    }

    /**
     * Validates and inserts a book.
     *
     * @param yearText the published year exactly as typed, so "20x4" becomes a form error
     */
    public int create(Book book, String yearText) throws ValidationException {
        Map<String, String> errors = prepareAndValidate(book, yearText);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return bookDao.create(book);
    }

    public void update(Book book, String yearText) throws ValidationException {
        if (bookDao.findById(book.getId()).isEmpty()) {
            throw new ValidationException("That book no longer exists.");
        }
        Map<String, String> errors = prepareAndValidate(book, yearText);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        bookDao.update(book);
    }

    /**
     * Deletes a book that is not on loan. Its past loan rows go through ON DELETE CASCADE.
     *
     * @return the deleted book (for the confirmation message)
     */
    public Book delete(int id) throws ValidationException {
        Book book = bookDao.findById(id)
                .orElseThrow(() -> new ValidationException("That book no longer exists."));
        if (loanDao.hasOpenLoan(id)) {
            throw new ValidationException(
                    book.getTitle() + " cannot be deleted because it is currently on loan.");
        }
        bookDao.delete(id);
        return book;
    }

    // ------------------------------------------------------------------ helpers

    private Map<String, String> prepareAndValidate(Book b, String yearText) {
        Map<String, String> errors = new LinkedHashMap<>();
        b.setIsbn(trim(b.getIsbn()));
        b.setTitle(trim(b.getTitle()));
        b.setAuthor(trim(b.getAuthor()));
        b.setGenre(trim(b.getGenre()));
        b.setDescription(trim(b.getDescription()));

        if (isBlank(b.getIsbn())) {
            errors.put("isbn", "ISBN is required.");
        } else if (!b.getIsbn().matches("[0-9Xx-]{10,17}")) {
            errors.put("isbn", "ISBN must be 10 to 17 characters: digits, hyphens or X.");
        }
        if (isBlank(b.getTitle())) {
            errors.put("title", "Title is required.");
        } else if (b.getTitle().length() > 200) {
            errors.put("title", "Title must be 200 characters or fewer.");
        }
        if (isBlank(b.getAuthor())) {
            errors.put("author", "Author is required.");
        } else if (b.getAuthor().length() > 150) {
            errors.put("author", "Author must be 150 characters or fewer.");
        }
        if (b.getGenre() != null && b.getGenre().length() > 50) {
            errors.put("genre", "Genre must be 50 characters or fewer.");
        }
        if (b.getDescription() != null && b.getDescription().length() > 500) {
            errors.put("description", "Description must be 500 characters or fewer.");
        }

        // Published year: optional, but if given it must be a whole number and not in the future.
        String year = trim(yearText);
        if (isBlank(year)) {
            b.setPublishedYear(null);
        } else if (!year.matches("\\d{1,4}")) {
            errors.put("publishedYear", "Published year must be a whole number, for example 2014.");
        } else {
            int y = Integer.parseInt(year);          // safe: at most 4 digits
            int thisYear = Year.now().getValue();
            if (y < 1 || y > thisYear) {
                errors.put("publishedYear",
                        "Published year must be between 1 and " + thisYear + " (it cannot be in the future).");
            } else {
                b.setPublishedYear(y);
            }
        }

        if (!errors.containsKey("isbn")) {
            bookDao.findByIsbn(b.getIsbn())
                    .filter(other -> other.getId() != b.getId())
                    .ifPresent(other -> errors.put("isbn",
                            "ISBN " + b.getIsbn() + " is already used by \"" + other.getTitle() + "\"."));
        }
        return errors;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
