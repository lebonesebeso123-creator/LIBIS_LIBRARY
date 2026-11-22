package com.libis.dao;

import com.libis.config.DataSourceProvider;
import com.libis.exception.DataAccessException;
import com.libis.model.Book;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDaoImpl implements BookDao {

    private static final Logger LOG = Logger.getLogger(BookDaoImpl.class.getName());

    /** Availability is derived: a book is available when it has no open loan row. */
    private static final String SELECT_WITH_AVAILABILITY =
            "SELECT b.book_id, b.isbn, b.title, b.author, b.genre, b.description, b.published_year,"
            + " NOT EXISTS (SELECT 1 FROM loans l"
            + "             WHERE l.book_id = b.book_id AND l.returned_on IS NULL) AS available"
            + " FROM books b";

    private static final String SQL_FIND_ALL = SELECT_WITH_AVAILABILITY + " ORDER BY b.title";
    private static final String SQL_FIND_BY_ID = SELECT_WITH_AVAILABILITY + " WHERE b.book_id = ?";
    private static final String SQL_FIND_BY_ISBN = SELECT_WITH_AVAILABILITY + " WHERE b.isbn = ?";
    private static final String SQL_SEARCH = SELECT_WITH_AVAILABILITY
            + " WHERE LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ? ORDER BY b.title";
    private static final String SQL_INSERT =
            "INSERT INTO books (isbn, title, author, genre, description, published_year)"
            + " VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE books SET isbn = ?, title = ?, author = ?, genre = ?, description = ?,"
            + " published_year = ? WHERE book_id = ?";
    private static final String SQL_DELETE = "DELETE FROM books WHERE book_id = ?";

    private final DataSource dataSource;

    public BookDaoImpl() {
        this(DataSourceProvider.getDataSource());
    }

    public BookDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Book> findAll() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            return mapAll(rs);
        } catch (SQLException e) {
            throw fail("Could not load books", e);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw fail("Could not load book", e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ISBN)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw fail("Could not load book", e);
        }
    }

    @Override
    public List<Book> search(String term) {
        String like = "%" + term.trim().toLowerCase() + "%";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SEARCH)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        } catch (SQLException e) {
            throw fail("Could not search books", e);
        }
    }

    @Override
    public int create(Book b) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            bindFields(ps, b);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw fail("Could not create book", e);
        }
    }

    @Override
    public boolean update(Book b) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            bindFields(ps, b);
            ps.setInt(7, b.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw fail("Could not update book", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw fail("Could not delete book", e);
        }
    }

    // ------------------------------------------------------------------ helpers

    private static void bindFields(PreparedStatement ps, Book b) throws SQLException {
        ps.setString(1, b.getIsbn());
        ps.setString(2, b.getTitle());
        ps.setString(3, b.getAuthor());
        ps.setString(4, emptyToNull(b.getGenre()));
        ps.setString(5, emptyToNull(b.getDescription()));
        if (b.getPublishedYear() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, b.getPublishedYear());
        }
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    private static List<Book> mapAll(ResultSet rs) throws SQLException {
        List<Book> list = new ArrayList<>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    private static Book map(ResultSet rs) throws SQLException {
        int year = rs.getInt("published_year");
        Integer publishedYear = rs.wasNull() ? null : year;
        return new Book(
                rs.getInt("book_id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getString("description"),
                publishedYear,
                rs.getBoolean("available"));
    }

    private static DataAccessException fail(String message, SQLException e) {
        LOG.log(Level.SEVERE, message, e);
        return new DataAccessException(message, e);
    }
}
