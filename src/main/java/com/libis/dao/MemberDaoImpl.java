package com.libis.dao;

import com.libis.config.DataSourceProvider;
import com.libis.exception.DataAccessException;
import com.libis.model.Member;
import com.libis.model.Role;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemberDaoImpl implements MemberDao {

    private static final Logger LOG = Logger.getLogger(MemberDaoImpl.class.getName());

    private static final String COLUMNS =
            "member_id, member_number, full_name, email, username, password, role";

    private static final String SQL_FIND_ALL =
            "SELECT " + COLUMNS + " FROM members ORDER BY member_number";
    private static final String SQL_FIND_BY_ID =
            "SELECT " + COLUMNS + " FROM members WHERE member_id = ?";
    private static final String SQL_FIND_BY_USERNAME =
            "SELECT " + COLUMNS + " FROM members WHERE username = ?";
    private static final String SQL_FIND_BY_NUMBER =
            "SELECT " + COLUMNS + " FROM members WHERE member_number = ?";
    private static final String SQL_FIND_BY_EMAIL =
            "SELECT " + COLUMNS + " FROM members WHERE email = ?";
    private static final String SQL_SEARCH =
            "SELECT " + COLUMNS + " FROM members"
            + " WHERE LOWER(full_name) LIKE ? OR LOWER(member_number) LIKE ?"
            + " ORDER BY member_number";
    private static final String SQL_INSERT =
            "INSERT INTO members (member_number, full_name, email, username, password, role)"
            + " VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_WITH_PASSWORD =
            "UPDATE members SET member_number = ?, full_name = ?, email = ?, username = ?,"
            + " role = ?, password = ? WHERE member_id = ?";
    private static final String SQL_UPDATE_KEEP_PASSWORD =
            "UPDATE members SET member_number = ?, full_name = ?, email = ?, username = ?,"
            + " role = ? WHERE member_id = ?";
    private static final String SQL_DELETE =
            "DELETE FROM members WHERE member_id = ?";

    private final DataSource dataSource;

    public MemberDaoImpl() {
        this(DataSourceProvider.getDataSource());
    }

    public MemberDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Member> findAll() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            return mapAll(rs);
        } catch (SQLException e) {
            throw fail("Could not load members", e);
        }
    }

    @Override
    public Optional<Member> findById(int id) {
        return findOne(SQL_FIND_BY_ID, ps -> ps.setInt(1, id));
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return findOne(SQL_FIND_BY_USERNAME, ps -> ps.setString(1, username));
    }

    @Override
    public Optional<Member> findByMemberNumber(String memberNumber) {
        return findOne(SQL_FIND_BY_NUMBER, ps -> ps.setString(1, memberNumber));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return findOne(SQL_FIND_BY_EMAIL, ps -> ps.setString(1, email));
    }

    @Override
    public List<Member> search(String term) {
        // The % wildcards go into the bound VALUE, never into the SQL string.
        String like = "%" + term.trim().toLowerCase() + "%";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SEARCH)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        } catch (SQLException e) {
            throw fail("Could not search members", e);
        }
    }

    @Override
    public int create(Member m) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getMemberNumber());
            ps.setString(2, m.getFullName());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getUsername());
            ps.setString(5, m.getPassword());
            ps.setString(6, m.getRole().value());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw fail("Could not create member", e);
        }
    }

    @Override
    public boolean update(Member m) {
        boolean changePassword = m.getPassword() != null && !m.getPassword().isBlank();
        String sql = changePassword ? SQL_UPDATE_WITH_PASSWORD : SQL_UPDATE_KEEP_PASSWORD;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, m.getMemberNumber());
            ps.setString(i++, m.getFullName());
            ps.setString(i++, m.getEmail());
            ps.setString(i++, m.getUsername());
            ps.setString(i++, m.getRole().value());
            if (changePassword) {
                ps.setString(i++, m.getPassword());
            }
            ps.setInt(i, m.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw fail("Could not update member", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw fail("Could not delete member", e);
        }
    }

    // ------------------------------------------------------------------ helpers

    @FunctionalInterface
    private interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<Member> findOne(String sql, Binder binder) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw fail("Could not load member", e);
        }
    }

    private static List<Member> mapAll(ResultSet rs) throws SQLException {
        List<Member> list = new ArrayList<>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    private static Member map(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("member_id"),
                rs.getString("member_number"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                Role.fromValue(rs.getString("role")));
    }

    private static DataAccessException fail(String message, SQLException e) {
        LOG.log(Level.SEVERE, message, e);
        return new DataAccessException(message, e);
    }
}
