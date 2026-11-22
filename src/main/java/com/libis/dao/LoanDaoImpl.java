package com.libis.dao;

import com.libis.config.DataSourceProvider;
import com.libis.exception.DataAccessException;
import com.libis.model.Loan;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanDaoImpl implements LoanDao {

    private static final Logger LOG = Logger.getLogger(LoanDaoImpl.class.getName());

    private static final String SELECT_WITH_BOOK =
            "SELECT l.loan_id, l.member_id, l.book_id, b.title, b.author, l.borrowed_on, l.returned_on"
            + " FROM loans l JOIN books b ON b.book_id = l.book_id";

    private static final String SQL_INSERT =
            "INSERT INTO loans (member_id, book_id, borrowed_on) VALUES (?, ?, ?)";
    private static final String SQL_MARK_RETURNED =
            "UPDATE loans SET returned_on = ? WHERE loan_id = ? AND returned_on IS NULL";
    private static final String SQL_COUNT_OPEN =
            "SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned_on IS NULL";
    private static final String SQL_FIND_OPEN = SELECT_WITH_BOOK
            + " WHERE l.member_id = ? AND l.returned_on IS NULL ORDER BY l.borrowed_on, l.loan_id";
    private static final String SQL_FIND_HISTORY = SELECT_WITH_BOOK
            + " WHERE l.member_id = ? AND l.returned_on IS NOT NULL"
            + " ORDER BY l.returned_on DESC, l.loan_id DESC";
    private static final String SQL_HAS_OPEN =
            "SELECT 1 FROM loans WHERE book_id = ? AND returned_on IS NULL LIMIT 1";

    private final DataSource dataSource;

    public LoanDaoImpl() {
        this(DataSourceProvider.getDataSource());
    }

    public LoanDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(int memberId, int bookId, LocalDate borrowedOn) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, bookId);
            ps.setDate(3, Date.valueOf(borrowedOn));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw fail("Could not record loan", e);
        }
    }

    @Override
    public boolean markReturned(int loanId, LocalDate returnedOn) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_MARK_RETURNED)) {
            ps.setDate(1, Date.valueOf(returnedOn));
            ps.setInt(2, loanId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw fail("Could not record return", e);
        }
    }

    @Override
    public int countOpenByMember(int memberId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_COUNT_OPEN)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw fail("Could not count loans", e);
        }
    }

    @Override
    public List<Loan> findOpenByMember(int memberId) {
        return findByMember(SQL_FIND_OPEN, memberId);
    }

    @Override
    public List<Loan> findHistoryByMember(int memberId) {
        return findByMember(SQL_FIND_HISTORY, memberId);
    }

    @Override
    public boolean hasOpenLoan(int bookId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_HAS_OPEN)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw fail("Could not check loan", e);
        }
    }

    // ------------------------------------------------------------------ helpers

    private List<Loan> findByMember(String sql, int memberId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Loan> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw fail("Could not load loans", e);
        }
    }

    private static Loan map(ResultSet rs) throws SQLException {
        Date borrowed = rs.getDate("borrowed_on");
        Date returned = rs.getDate("returned_on");
        return new Loan(
                rs.getInt("loan_id"),
                rs.getInt("member_id"),
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                borrowed == null ? null : borrowed.toLocalDate(),
                returned == null ? null : returned.toLocalDate());
    }

    private static DataAccessException fail(String message, SQLException e) {
        LOG.log(Level.SEVERE, message, e);
        return new DataAccessException(message, e);
    }
}
