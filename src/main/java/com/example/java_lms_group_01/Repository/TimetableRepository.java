package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {

    // Find Timetable (with filters)
    public List<Timetable> findByFilters(String department, String day, String keyword) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM timetable WHERE 1=1";

        List<String> params = new ArrayList<>();

        if (department != null && !department.isEmpty()) {
            sql += " AND department=?";
            params.add(department);
        }

        if (day != null && !day.isEmpty()) {
            sql += " AND day=?";
            params.add(day);
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND (time_table_id LIKE ? OR courseCode LIKE ? OR lec_id LIKE ?)";
            String k = "%" + keyword + "%";
            params.add(k);
            params.add(k);
            params.add(k);
        }

        sql += " ORDER BY time_table_id DESC";

        PreparedStatement stmt = con.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            stmt.setString(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();

        List<Timetable> list = new ArrayList<>();

        while (rs.next()) {

            Timetable t = new Timetable(
                    rs.getString("time_table_id"),
                    rs.getString("department"),
                    rs.getString("lec_id"),
                    rs.getString("courseCode"),
                    rs.getString("admin_id"),
                    rs.getString("day"),
                    rs.getTime("start_time") == null ? null : rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time") == null ? null : rs.getTime("end_time").toLocalTime(),
                    rs.getString("session_type")
            );

            list.add(t);
        }

        return list;
    }

    // Get all departments
    public List<String> findAllDepartments() throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "SELECT DISTINCT department FROM timetable"
        );

        ResultSet rs = stmt.executeQuery();

        List<String> list = new ArrayList<>();

        while (rs.next()) {
            String dep = rs.getString("department");
            if (dep != null && !dep.isEmpty()) {
                list.add(dep);
            }
        }

        return list;
    }

    // Get all days
    public List<String> findAllDays() throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "SELECT DISTINCT day FROM timetable"
        );

        ResultSet rs = stmt.executeQuery();

        List<String> list = new ArrayList<>();

        while (rs.next()) {
            String day = rs.getString("day");
            if (day != null && !day.isEmpty()) {
                list.add(day);
            }
        }

        return list;
    }

    // Save Timetable
    public boolean save(Timetable t) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO timetable VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        stmt.setString(1, t.getTimeTableId());
        stmt.setString(2, t.getDepartment());
        stmt.setString(3, t.getLecId());
        stmt.setString(4, t.getCourseCode());
        stmt.setString(5, t.getAdminId());
        stmt.setString(6, t.getDay());
        stmt.setTime(7, t.getStartTime() == null ? null : Time.valueOf(t.getStartTime()));
        stmt.setTime(8, t.getEndTime() == null ? null : Time.valueOf(t.getEndTime()));
        stmt.setString(9, t.getSessionType());

        return stmt.executeUpdate() > 0;
    }

    // Update Timetable
    public boolean update(Timetable t) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "UPDATE timetable SET department=?, lec_id=?, courseCode=?, admin_id=?, day=?, start_time=?, end_time=?, session_type=? WHERE time_table_id=?"
        );

        stmt.setString(1, t.getDepartment());
        stmt.setString(2, t.getLecId());
        stmt.setString(3, t.getCourseCode());
        stmt.setString(4, t.getAdminId());
        stmt.setString(5, t.getDay());
        stmt.setTime(6, t.getStartTime() == null ? null : Time.valueOf(t.getStartTime()));
        stmt.setTime(7, t.getEndTime() == null ? null : Time.valueOf(t.getEndTime()));
        stmt.setString(8, t.getSessionType());
        stmt.setString(9, t.getTimeTableId());

        return stmt.executeUpdate() > 0;
    }

    // Delete Timetable
    public boolean deleteById(String id) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM timetable WHERE time_table_id=?"
        );

        stmt.setString(1, id);

        return stmt.executeUpdate() > 0;
    }
}