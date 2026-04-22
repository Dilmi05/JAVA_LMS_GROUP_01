package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.*;
import com.example.java_lms_group_01.model.request.*;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicalOfficerRepository {

    // EXAM ATTENDANCE
    public void addExamAttendance(ExamAttendanceRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO exam_attendance (studentReg, courseCode, status, attendanceDate) VALUES (?, ?, ?, ?)"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setString(3, r.getStatus());
        stmt.setDate(4, Date.valueOf(r.getAttendanceDate()));

        stmt.executeUpdate();
    }

    public void updateExamAttendance(int id, ExamAttendanceRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "UPDATE exam_attendance SET studentReg=?, courseCode=?, status=?, attendanceDate=? WHERE exam_attendance_id=?"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setString(3, r.getStatus());
        stmt.setDate(4, Date.valueOf(r.getAttendanceDate()));
        stmt.setInt(5, id);

        stmt.executeUpdate();
    }

    public void deleteExamAttendance(int id) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM exam_attendance WHERE exam_attendance_id=?"
        );

        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public List<ExamAttendance> findExamAttendance(String keyword) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM exam_attendance";

        if (keyword != null && !keyword.isEmpty()) {
            sql += " WHERE studentReg LIKE ? OR courseCode LIKE ?";
        }

        sql += " ORDER BY exam_attendance_id DESC";

        PreparedStatement stmt = con.prepareStatement(sql);

        if (keyword != null && !keyword.isEmpty()) {
            String k = "%" + keyword + "%";
            stmt.setString(1, k);
            stmt.setString(2, k);
        }

        ResultSet rs = stmt.executeQuery();

        List<ExamAttendance> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new ExamAttendance(
                    String.valueOf(rs.getInt("exam_attendance_id")),
                    rs.getString("studentReg"),
                    rs.getString("courseCode"),
                    rs.getString("status"),
                    rs.getDate("attendanceDate") == null ? "" : rs.getDate("attendanceDate").toString()
            ));
        }

        return list;
    }


    // ATTENDANCE
    public void addAttendance(AttendanceRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO attendance (StudentReg, courseCode, tech_officer_reg, SubmissionDate, session_type, attendance_status) VALUES (?, ?, ?, ?, ?, ?)"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setString(3, r.getTechOfficerReg());
        stmt.setDate(4, Date.valueOf(r.getSubmissionDate()));
        stmt.setString(5, r.getSessionType());
        stmt.setString(6, r.getStatus());

        stmt.executeUpdate();
    }

    public void updateAttendance(int id, AttendanceRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "UPDATE attendance SET StudentReg=?, courseCode=?, SubmissionDate=?, session_type=?, attendance_status=?, tech_officer_reg=? WHERE attendance_id=?"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setDate(3, Date.valueOf(r.getSubmissionDate()));
        stmt.setString(4, r.getSessionType());
        stmt.setString(5, r.getStatus());
        stmt.setString(6, r.getTechOfficerReg());
        stmt.setInt(7, id);

        stmt.executeUpdate();
    }

    public void deleteAttendance(int id) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM attendance WHERE attendance_id=?"
        );

        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public List<Attendance> findAttendance(String keyword) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM attendance";

        if (keyword != null && !keyword.isEmpty()) {
            sql += " WHERE StudentReg LIKE ? OR courseCode LIKE ?";
        }

        sql += " ORDER BY attendance_id DESC";

        PreparedStatement stmt = con.prepareStatement(sql);

        if (keyword != null && !keyword.isEmpty()) {
            String k = "%" + keyword + "%";
            stmt.setString(1, k);
            stmt.setString(2, k);
        }

        ResultSet rs = stmt.executeQuery();

        List<Attendance> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new Attendance(
                    String.valueOf(rs.getInt("attendance_id")),
                    rs.getString("StudentReg"),
                    rs.getString("courseCode"),
                    rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                    rs.getString("session_type"),
                    rs.getString("attendance_status"),
                    rs.getString("tech_officer_reg")
            ));
        }

        return list;
    }


    // MEDICAL
    public void addMedical(MedicalRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO medical (StudentReg, courseCode, tech_officer_reg, SubmissionDate, Description, session_type, attendance_id, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?, 'pending')"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setString(3, r.getTechOfficerReg());
        stmt.setDate(4, Date.valueOf(r.getSubmissionDate()));
        stmt.setString(5, r.getDescription());
        stmt.setString(6, r.getSessionType());
        stmt.setInt(7, r.getAttendanceId());

        stmt.executeUpdate();

        PreparedStatement update = con.prepareStatement(
                "UPDATE attendance SET attendance_status='medical' WHERE attendance_id=?"
        );

        update.setInt(1, r.getAttendanceId());
        update.executeUpdate();
    }

    public void updateMedical(int id, MedicalRequest r) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "UPDATE medical SET StudentReg=?, courseCode=?, tech_officer_reg=?, SubmissionDate=?, Description=?, session_type=?, attendance_id=? WHERE medical_id=?"
        );

        stmt.setString(1, r.getStudentRegNo());
        stmt.setString(2, r.getCourseCode());
        stmt.setString(3, r.getTechOfficerReg());
        stmt.setDate(4, Date.valueOf(r.getSubmissionDate()));
        stmt.setString(5, r.getDescription());
        stmt.setString(6, r.getSessionType());
        stmt.setInt(7, r.getAttendanceId());
        stmt.setInt(8, id);

        stmt.executeUpdate();
    }

    public void deleteMedical(int medicalId, int attendanceId) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement m = con.prepareStatement(
                "DELETE FROM medical WHERE medical_id=?"
        );

        m.setInt(1, medicalId);
        m.executeUpdate();

        PreparedStatement a = con.prepareStatement(
                "UPDATE attendance SET attendance_status='absent' WHERE attendance_id=?"
        );

        a.setInt(1, attendanceId);
        a.executeUpdate();
    }

    public List<Medical> findMedical(String keyword) throws SQLException {

        List<Medical> list = new ArrayList<>();

        String sql = "SELECT medical_id, StudentReg, courseCode, SubmissionDate, Description, session_type, attendance_id, tech_officer_reg, approval_status " +
                "FROM medical " +
                "WHERE StudentReg LIKE ? OR courseCode LIKE ? " +
                "ORDER BY medical_id DESC";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        String search = "%" + (keyword == null ? "" : keyword) + "%";

        stmt.setString(1, search);
        stmt.setString(2, search);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            Medical m = new Medical(
                    String.valueOf(rs.getInt("medical_id")),
                    rs.getString("StudentReg"),
                    rs.getString("courseCode"),
                    rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                    rs.getString("Description"),
                    rs.getString("session_type"),
                    String.valueOf(rs.getInt("attendance_id")),
                    rs.getString("approval_status"),
                    rs.getString("tech_officer_reg")
            );

            list.add(m);
        }

        return list;
    }


    // COUNTS of Attendance AND Medical AND Notices
    public int countAttendance() throws SQLException {
        return count("attendance");
    }

    public int countMedical() throws SQLException {
        return count("medical");
    }

    public int countNotices() throws SQLException {
        return count("notice");
    }

    private int count(String table) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM " + table
        );

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }
}