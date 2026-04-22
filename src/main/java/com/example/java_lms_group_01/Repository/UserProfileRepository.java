package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.UserRecord;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.PasswordUtil;

import java.sql.*;

public class UserProfileRepository {

    private final UserImageRepository imageRepo = new UserImageRepository();

    // Find Student Profile
    public UserRecord findStudentProfile(String regNo) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM users u JOIN student s ON u.user_id = s.registrationNo WHERE s.registrationNo=?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, regNo);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) return null;

        return new UserRecord(
                rs.getString("user_id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("phoneNumber"),
                null,
                null,
                "Student",
                rs.getString("user_id"),
                null,
                rs.getString("department"),
                rs.getString("batch"),
                rs.getObject("GPA") == null ? null : ((Number) rs.getObject("GPA")).doubleValue(),
                rs.getString("status"),
                null,
                imageRepo.findImagePathByUserId(con, regNo)
        );
    }

    // Find Lecturer Profile
    public UserRecord findLecturerProfile(String regNo) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM users u JOIN lecturer l ON u.user_id = l.registrationNo WHERE l.registrationNo=?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, regNo);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) return null;

        return new UserRecord(
                rs.getString("user_id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("phoneNumber"),
                null,
                null,
                "Lecturer",
                rs.getString("user_id"),
                null,
                rs.getString("department"),
                null,
                null,
                null,
                rs.getString("position"),
                imageRepo.findImagePathByUserId(con, regNo)
        );
    }

    // Find Technical Officer Profile
    public UserRecord findTechnicalOfficerProfile(String regNo) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM users u JOIN tech_officer t ON u.user_id = t.registrationNo WHERE t.registrationNo=?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, regNo);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) return null;

        return new UserRecord(
                rs.getString("user_id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("phoneNumber"),
                null,
                null,
                "TechnicalOfficer",
                rs.getString("user_id"),
                null,
                null,
                null,
                null,
                null,
                null,
                imageRepo.findImagePathByUserId(con, regNo)
        );
    }

    //  Update Student Profile
    public void updateStudentProfile(String regNo, String email, String phone, String address,
                                     String image, String currentPw, String newPw) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE users SET email=?, phoneNumber=?, address=? WHERE user_id=?"
            );

            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setString(4, regNo);
            stmt.executeUpdate();

            // password update
            if (newPw != null && !newPw.isEmpty()) {

                PreparedStatement check = con.prepareStatement(
                        "SELECT password FROM student WHERE registrationNo=?"
                );

                check.setString(1, regNo);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    String stored = rs.getString("password");

                    if (!PasswordUtil.matches(currentPw, stored)) {
                        throw new IllegalArgumentException("Wrong current password");
                    }
                }

                PreparedStatement p = con.prepareStatement(
                        "UPDATE student SET password=? WHERE registrationNo=?"
                );

                p.setString(1, PasswordUtil.hashPassword(newPw));
                p.setString(2, regNo);
                p.executeUpdate();
            }

            imageRepo.upsertImagePath(con, regNo, image);

        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    // Update Lecturer Profile
    public void updateLecturerProfile(String regNo, String first, String last, String email,
                                      String address, String phone, String dep, String pos, String image) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement user = con.prepareStatement(
                    "UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=? WHERE user_id=?"
            );

            user.setString(1, first);
            user.setString(2, last);
            user.setString(3, email);
            user.setString(4, address);
            user.setString(5, phone);
            user.setString(6, regNo);
            user.executeUpdate();

            PreparedStatement lec = con.prepareStatement(
                    "UPDATE lecturer SET department=?, position=? WHERE registrationNo=?"
            );

            lec.setString(1, dep);
            lec.setString(2, pos);
            lec.setString(3, regNo);
            lec.executeUpdate();

            imageRepo.upsertImagePath(con, regNo, image);

        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    // Update Technical Officer Profile
    public void updateTechnicalOfficerProfile(String regNo, String first, String last,
                                              String email, String phone, String address, String image) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE users SET firstName=?, lastName=?, email=?, phoneNumber=?, address=? WHERE user_id=?"
            );

            stmt.setString(1, first);
            stmt.setString(2, last);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, regNo);
            stmt.executeUpdate();

            imageRepo.upsertImagePath(con, regNo, image);

        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
}