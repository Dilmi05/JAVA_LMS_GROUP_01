package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.UserManagementRow;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public List<UserManagementRow> findAdmins() throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.address, u.phone_number, u.date_of_birth, u.gender, " +
                "'Admin' AS role, NULL AS registration_no, a.dept_id, NULL AS batch_id, NULL AS status, " +
                "NULL AS position, a.date_of_appointment, a.access_level, NULL AS date_of_joining, " +
                "NULL AS lab_assigned, NULL AS shift, NULL AS qualifications " +
                "FROM `user` u INNER JOIN admin a ON a.user_id = u.user_id ORDER BY u.user_id DESC";
        return executeQuery(sql);
    }

    public List<UserManagementRow> findLecturers() throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.address, u.phone_number, u.date_of_birth, u.gender, " +
                "'Lecturer' AS role, l.registration_no, l.dept_id, NULL AS batch_id, NULL AS status, " +
                "l.position, NULL AS date_of_appointment, NULL AS access_level, l.date_of_joining, " +
                "NULL AS lab_assigned, NULL AS shift, NULL AS qualifications " +
                "FROM `user` u INNER JOIN lecturer l ON l.user_id = u.user_id ORDER BY u.user_id DESC";
        return executeQuery(sql);
    }

    public List<UserManagementRow> findStudents() throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.address, u.phone_number, u.date_of_birth, u.gender, " +
                "'Student' AS role, s.registration_no, s.dept_id, s.batch_id, s.status, " +
                "NULL AS position, NULL AS date_of_appointment, NULL AS access_level, NULL AS date_of_joining, " +
                "NULL AS lab_assigned, NULL AS shift, NULL AS qualifications " +
                "FROM `user` u INNER JOIN student s ON s.user_id = u.user_id ORDER BY u.user_id DESC";
        return executeQuery(sql);
    }

    public List<UserManagementRow> findTechnicalOfficers() throws SQLException {
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.address, u.phone_number, u.date_of_birth, u.gender, " +
                "'TechnicalOfficer' AS role, NULL AS registration_no, t.dept_id, NULL AS batch_id, NULL AS status, " +
                "t.position, NULL AS date_of_appointment, NULL AS access_level, NULL AS date_of_joining, " +
                "t.lab_assigned, t.shift, t.qualifications " +
                "FROM `user` u INNER JOIN technicalofficer t ON t.user_id = u.user_id ORDER BY u.user_id DESC";
        return executeQuery(sql);
    }

    public boolean createAdmin(UserManagementRow row) throws SQLException {
        String roleSql = "INSERT INTO admin (user_id, dept_id, date_of_appointment, access_level) VALUES (?, ?, ?, ?)";
        return createWithRole(row, roleSql, stmt -> {
            stmt.setInt(2, requiredInt(row.getDeptId(), "Department ID"));
            stmt.setDate(3, Date.valueOf(requiredDate(row.getDateOfAppointment(), "Date of appointment")));
            stmt.setString(4, emptyToNull(row.getAccessLevel()));
        });
    }

    public boolean createLecturer(UserManagementRow row) throws SQLException {
        String roleSql = "INSERT INTO lecturer (user_id, registration_no, dept_id, position, date_of_joining) VALUES (?, ?, ?, ?, ?)";
        return createWithRole(row, roleSql, stmt -> {
            stmt.setString(2, requiredText(row.getRegistrationNo(), "Registration No"));
            stmt.setInt(3, requiredInt(row.getDeptId(), "Department ID"));
            stmt.setString(4, requiredText(row.getPosition(), "Position"));
            stmt.setDate(5, Date.valueOf(requiredDate(row.getDateOfJoining(), "Date of joining")));
        });
    }

    public boolean createStudent(UserManagementRow row) throws SQLException {
        String roleSql = "INSERT INTO student (user_id, registration_no, batch_id, dept_id, status) VALUES (?, ?, ?, ?, ?)";
        return createWithRole(row, roleSql, stmt -> {
            stmt.setString(2, requiredText(row.getRegistrationNo(), "Registration No"));
            if (row.getBatchId() == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, row.getBatchId());
            }
            stmt.setInt(4, requiredInt(row.getDeptId(), "Department ID"));
            stmt.setString(5, requiredText(row.getStatus(), "Status"));
        });
    }

    public boolean createTechnicalOfficer(UserManagementRow row) throws SQLException {
        String roleSql = "INSERT INTO technicalofficer (user_id, dept_id, position, lab_assigned, shift, qualifications) VALUES (?, ?, ?, ?, ?, ?)";
        return createWithRole(row, roleSql, stmt -> {
            stmt.setInt(2, requiredInt(row.getDeptId(), "Department ID"));
            stmt.setString(3, requiredText(row.getPosition(), "Position"));
            stmt.setString(4, emptyToNull(row.getLabAssigned()));
            stmt.setString(5, emptyToNull(row.getShift()));
            stmt.setString(6, emptyToNull(row.getQualifications()));
        });
    }

    public boolean updateAdmin(UserManagementRow row) throws SQLException {
        return updateWithRole(
                row,
                "UPDATE admin SET dept_id = ?, date_of_appointment = ?, access_level = ? WHERE user_id = ?",
                stmt -> {
                    stmt.setInt(1, requiredInt(row.getDeptId(), "Department ID"));
                    stmt.setDate(2, Date.valueOf(requiredDate(row.getDateOfAppointment(), "Date of appointment")));
                    stmt.setString(3, emptyToNull(row.getAccessLevel()));
                    stmt.setInt(4, row.getUserId());
                }
        );
    }

    public boolean updateLecturer(UserManagementRow row) throws SQLException {
        return updateWithRole(
                row,
                "UPDATE lecturer SET registration_no = ?, dept_id = ?, position = ?, date_of_joining = ? WHERE user_id = ?",
                stmt -> {
                    stmt.setString(1, requiredText(row.getRegistrationNo(), "Registration No"));
                    stmt.setInt(2, requiredInt(row.getDeptId(), "Department ID"));
                    stmt.setString(3, requiredText(row.getPosition(), "Position"));
                    stmt.setDate(4, Date.valueOf(requiredDate(row.getDateOfJoining(), "Date of joining")));
                    stmt.setInt(5, row.getUserId());
                }
        );
    }

    public boolean updateStudent(UserManagementRow row) throws SQLException {
        return updateWithRole(
                row,
                "UPDATE student SET registration_no = ?, batch_id = ?, dept_id = ?, status = ? WHERE user_id = ?",
                stmt -> {
                    stmt.setString(1, requiredText(row.getRegistrationNo(), "Registration No"));
                    if (row.getBatchId() == null) {
                        stmt.setNull(2, Types.INTEGER);
                    } else {
                        stmt.setInt(2, row.getBatchId());
                    }
                    stmt.setInt(3, requiredInt(row.getDeptId(), "Department ID"));
                    stmt.setString(4, requiredText(row.getStatus(), "Status"));
                    stmt.setInt(5, row.getUserId());
                }
        );
    }

    public boolean updateTechnicalOfficer(UserManagementRow row) throws SQLException {
        return updateWithRole(
                row,
                "UPDATE technicalofficer SET dept_id = ?, position = ?, lab_assigned = ?, shift = ?, qualifications = ? WHERE user_id = ?",
                stmt -> {
                    stmt.setInt(1, requiredInt(row.getDeptId(), "Department ID"));
                    stmt.setString(2, requiredText(row.getPosition(), "Position"));
                    stmt.setString(3, emptyToNull(row.getLabAssigned()));
                    stmt.setString(4, emptyToNull(row.getShift()));
                    stmt.setString(5, emptyToNull(row.getQualifications()));
                    stmt.setInt(6, row.getUserId());
                }
        );
    }

    public boolean deleteAdmin(int userId) throws SQLException {
        return deleteWithRole(userId, "DELETE FROM admin WHERE user_id = ?");
    }

    public boolean deleteLecturer(int userId) throws SQLException {
        return deleteWithRole(userId, "DELETE FROM lecturer WHERE user_id = ?");
    }

    public boolean deleteStudent(int userId) throws SQLException {
        return deleteWithRole(userId, "DELETE FROM student WHERE user_id = ?");
    }

    public boolean deleteTechnicalOfficer(int userId) throws SQLException {
        return deleteWithRole(userId, "DELETE FROM technicalofficer WHERE user_id = ?");
    }

    private boolean createWithRole(UserManagementRow row, String roleInsertSql, RoleStatementWriter writer) throws SQLException {
        String userSql = "INSERT INTO `user` (first_name, last_name, email, address, phone_number, date_of_birth, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            int userId;
            try (PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                fillUserStatement(userStmt, row, false);
                userStmt.executeUpdate();
                try (ResultSet keys = userStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to create user id.");
                    }
                    userId = keys.getInt(1);
                }
            }

            try (PreparedStatement roleStmt = connection.prepareStatement(roleInsertSql)) {
                roleStmt.setInt(1, userId);
                writer.write(roleStmt);
                roleStmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private boolean updateWithRole(UserManagementRow row, String roleUpdateSql, RoleStatementWriter writer) throws SQLException {
        String userSql = "UPDATE `user` SET first_name = ?, last_name = ?, email = ?, address = ?, phone_number = ?, date_of_birth = ?, gender = ? WHERE user_id = ?";

        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                fillUserStatement(userStmt, row, true);
                userStmt.executeUpdate();
            }

            try (PreparedStatement roleStmt = connection.prepareStatement(roleUpdateSql)) {
                writer.write(roleStmt);
                roleStmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private boolean deleteWithRole(int userId, String roleDeleteSql) throws SQLException {
        String userSql = "DELETE FROM `user` WHERE user_id = ?";

        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement roleStmt = connection.prepareStatement(roleDeleteSql)) {
                roleStmt.setInt(1, userId);
                roleStmt.executeUpdate();
            }

            int affected;
            try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                userStmt.setInt(1, userId);
                affected = userStmt.executeUpdate();
            }

            connection.commit();
            return affected > 0;
        } catch (Exception e) {
            connection.rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void fillUserStatement(PreparedStatement stmt, UserManagementRow row, boolean includeUserId) throws SQLException {
        stmt.setString(1, requiredText(row.getFirstName(), "First name"));
        stmt.setString(2, requiredText(row.getLastName(), "Last name"));
        stmt.setString(3, requiredText(row.getEmail(), "Email"));
        stmt.setString(4, emptyToNull(row.getAddress()));
        stmt.setString(5, emptyToNull(row.getPhoneNumber()));
        if (row.getDateOfBirth() == null) {
            stmt.setNull(6, Types.DATE);
        } else {
            stmt.setDate(6, Date.valueOf(row.getDateOfBirth()));
        }
        stmt.setString(7, emptyToNull(row.getGender()));
        if (includeUserId) {
            stmt.setInt(8, row.getUserId());
        }
    }

    private List<UserManagementRow> executeQuery(String sql) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<UserManagementRow> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(mapRow(rs));
            }
            return rows;
        }
    }

    private UserManagementRow mapRow(ResultSet rs) throws SQLException {
        Object dept = rs.getObject("dept_id");
        Object batch = rs.getObject("batch_id");
        Date dob = rs.getDate("date_of_birth");
        Date doa = rs.getDate("date_of_appointment");
        Date doj = rs.getDate("date_of_joining");

        return new UserManagementRow(
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("phone_number"),
                dob == null ? null : dob.toLocalDate(),
                rs.getString("gender"),
                rs.getString("role"),
                rs.getString("registration_no"),
                dept == null ? null : ((Number) dept).intValue(),
                batch == null ? null : ((Number) batch).intValue(),
                rs.getString("status"),
                rs.getString("position"),
                doa == null ? null : doa.toLocalDate(),
                rs.getString("access_level"),
                doj == null ? null : doj.toLocalDate(),
                rs.getString("lab_assigned"),
                rs.getString("shift"),
                rs.getString("qualifications")
        );
    }

    private String requiredText(String text, String field) {
        if (text == null || text.trim().isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return text.trim();
    }

    private int requiredInt(Integer value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be a positive number.");
        }
        return value;
    }

    private java.time.LocalDate requiredDate(java.time.LocalDate value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value;
    }

    private String emptyToNull(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @FunctionalInterface
    private interface RoleStatementWriter {
        void write(PreparedStatement stmt) throws SQLException;
    }
}
