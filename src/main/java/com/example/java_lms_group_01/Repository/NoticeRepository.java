package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoticeRepository {

    private static final String BASE_SELECT =
            "SELECT noticeId, title, content, publishDate, createdBy_adminId FROM Notices";

    public List<Notice> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY publishDate DESC, noticeId DESC";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Notice> notices = new ArrayList<>();
            while (rs.next()) {
                notices.add(mapRow(rs));
            }
            return notices;
        }
    }

    public List<Notice> findByKeyword(String keyword) throws SQLException {
        String sql = BASE_SELECT +
                " WHERE (? IS NULL OR ? = '' OR title LIKE ? OR content LIKE ?)" +
                " ORDER BY publishDate DESC, noticeId DESC";

        String safeKeyword = keyword == null ? "" : keyword.trim();
        String pattern = "%" + safeKeyword + "%";

        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, safeKeyword);
            statement.setString(2, safeKeyword);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Notice> notices = new ArrayList<>();
                while (rs.next()) {
                    notices.add(mapRow(rs));
                }
                return notices;
            }
        }
    }

    public boolean save(Notice notice) throws SQLException {
        String sql = "INSERT INTO Notices (title, content, publishDate, createdBy_adminId) VALUES (?, ?, ?, ?)";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, notice.getTitle());
            statement.setString(2, notice.getContent());
            statement.setDate(3, Date.valueOf(notice.getPublishDate()));
            statement.setInt(4, notice.getCreatedByAdminId());

            boolean inserted = statement.executeUpdate() > 0;
            if (!inserted) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    notice.setNoticeId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean update(Notice notice) throws SQLException {
        String sql = "UPDATE Notices SET title = ?, content = ?, publishDate = ?, createdBy_adminId = ? WHERE noticeId = ?";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getTitle());
            statement.setString(2, notice.getContent());
            statement.setDate(3, Date.valueOf(notice.getPublishDate()));
            statement.setInt(4, notice.getCreatedByAdminId());
            statement.setInt(5, notice.getNoticeId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(int noticeId) throws SQLException {
        String sql = "DELETE FROM Notices WHERE noticeId = ?";
        Connection connection = DBConnection.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, noticeId);
            return statement.executeUpdate() > 0;
        }
    }

    private Notice mapRow(ResultSet rs) throws SQLException {
        Date publishDate = rs.getDate("publishDate");
        return new Notice(
                rs.getInt("noticeId"),
                rs.getString("title"),
                rs.getString("content"),
                publishDate == null ? null : publishDate.toLocalDate(),
                rs.getInt("createdBy_adminId")
        );
    }
}
