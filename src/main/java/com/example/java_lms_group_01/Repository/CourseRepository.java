package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public List<Course> findByFilters(String department, String keyword) throws SQLException {

        String sql = "SELECT * FROM course WHERE 1=1";

        List<String> params = new ArrayList<>();

        if (department != null && !department.isEmpty()) {
            sql += " AND department = ?";
            params.add(department);
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND (courseCode LIKE ? OR name LIKE ?)";
            String pattern = "%" + keyword + "%";
            params.add(pattern);
            params.add(pattern);
        }

        sql += " ORDER BY courseCode";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            stm.setString(i + 1, params.get(i));
        }

        ResultSet rs = stm.executeQuery();

        List<Course> list = new ArrayList<>();

        while (rs.next()) {
            Course c = new Course(
                    rs.getString("courseCode"),
                    rs.getString("name"),
                    rs.getString("lecturerRegistrationNo"),
                    rs.getString("department"),
                    rs.getString("semester"),
                    rs.getInt("credit"),
                    rs.getString("course_type")
            );

            list.add(c);
        }

        return list;
    }

    public List<String> findAllDepartments() throws SQLException {

        String sql = "SELECT DISTINCT department FROM course";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        ResultSet rs = stm.executeQuery();

        List<String> list = new ArrayList<>();

        while (rs.next()) {
            list.add(rs.getString("department"));
        }

        return list;
    }

    public boolean save(Course c) throws SQLException {

        String sql = "INSERT INTO course VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setString(1, c.getCourseCode());
        stm.setString(2, c.getName());
        stm.setString(3, c.getLecturerRegistrationNo());
        stm.setString(4, c.getDepartment());
        stm.setString(5, c.getSemester());
        stm.setInt(6, c.getCredit());
        stm.setString(7, c.getCourseType());

        return stm.executeUpdate() > 0;
    }

    public boolean update(Course c) throws SQLException {

        String sql = "UPDATE course SET name=?, lecturerRegistrationNo=?, department=?, semester=?, credit=?, course_type=? WHERE courseCode=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setString(1, c.getName());
        stm.setString(2, c.getLecturerRegistrationNo());
        stm.setString(3, c.getDepartment());
        stm.setString(4, c.getSemester());
        stm.setInt(5, c.getCredit());
        stm.setString(6, c.getCourseType());
        stm.setString(7, c.getCourseCode());

        return stm.executeUpdate() > 0;
    }

    public boolean deleteByCourseCode(String courseCode) throws SQLException {

        String sql = "DELETE FROM course WHERE courseCode=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setString(1, courseCode);

        return stm.executeUpdate() > 0;
    }
}
