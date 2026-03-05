package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {

    private static final String BASE_SELECT =
            "SELECT timetableId, departmentId, semester, academicYear FROM Timetables";

    public List<Timetable> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY timetableId DESC";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Timetable> timetables = new ArrayList<>();
            while (rs.next()) {
                timetables.add(mapRow(rs));
            }
            return timetables;
        }
    }

    public List<Timetable> findByFilters(Integer departmentId, Integer semester, String academicYearKeyword) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departmentId != null) {
            sql.append(" AND departmentId = ?");
            params.add(departmentId);
        }

        if (semester != null) {
            sql.append(" AND semester = ?");
            params.add(semester);
        }

        if (academicYearKeyword != null && !academicYearKeyword.isBlank()) {
            sql.append(" AND academicYear LIKE ?");
            params.add("%" + academicYearKeyword.trim() + "%");
        }

        sql.append(" ORDER BY timetableId DESC");

        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<Timetable> timetables = new ArrayList<>();
                while (rs.next()) {
                    timetables.add(mapRow(rs));
                }
                return timetables;
            }
        }
    }

    public List<Integer> findAllDepartmentIds() throws SQLException {
        String sql = "SELECT DISTINCT departmentId FROM Timetables ORDER BY departmentId";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Integer> departmentIds = new ArrayList<>();
            while (rs.next()) {
                departmentIds.add(rs.getInt("departmentId"));
            }
            return departmentIds;
        }
    }

    public List<Integer> findAllSemesters() throws SQLException {
        String sql = "SELECT DISTINCT semester FROM Timetables ORDER BY semester";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Integer> semesters = new ArrayList<>();
            while (rs.next()) {
                semesters.add(rs.getInt("semester"));
            }
            return semesters;
        }
    }

    public boolean save(Timetable timetable) throws SQLException {
        String sql = "INSERT INTO Timetables (departmentId, semester, academicYear) VALUES (?, ?, ?)";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, timetable.getDepartmentId());
            statement.setInt(2, timetable.getSemester());
            statement.setString(3, timetable.getAcademicYear());

            boolean inserted = statement.executeUpdate() > 0;
            if (!inserted) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    timetable.setTimetableId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean update(Timetable timetable) throws SQLException {
        String sql = "UPDATE Timetables SET departmentId = ?, semester = ?, academicYear = ? WHERE timetableId = ?";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, timetable.getDepartmentId());
            statement.setInt(2, timetable.getSemester());
            statement.setString(3, timetable.getAcademicYear());
            statement.setInt(4, timetable.getTimetableId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(int timetableId) throws SQLException {
        String sql = "DELETE FROM Timetables WHERE timetableId = ?";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, timetableId);
            return statement.executeUpdate() > 0;
        }
    }

    private Timetable mapRow(ResultSet rs) throws SQLException {
        return new Timetable(
                rs.getInt("timetableId"),
                rs.getInt("departmentId"),
                rs.getInt("semester"),
                rs.getString("academicYear")
        );
    }
}
