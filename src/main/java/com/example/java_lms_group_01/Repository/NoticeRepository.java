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

    public List<Notice> findAll() throws SQLException {

        String sql = "SELECT * FROM notice ORDER BY publishDate DESC, notice_id DESC";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        ResultSet rs = stm.executeQuery();

        List<Notice> list = new ArrayList<>();

        while (rs.next()) {

            Date publishDate = rs.getDate("publishDate");

            Notice n = new Notice(
                    rs.getInt("notice_id"),
                    rs.getString("notice_title"),
                    rs.getString("notice_content"),
                    publishDate == null ? null : publishDate.toLocalDate(),
                    rs.getString("createdBy")
            );

            list.add(n);
        }

        return list;
    }

    public List<Notice> findByKeyword(String keyword) throws SQLException {

        String sql = "SELECT * FROM notice WHERE notice_title LIKE ? OR notice_content LIKE ? " +
                "ORDER BY publishDate DESC, notice_id DESC";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        String pattern = "%" + (keyword == null ? "" : keyword) + "%";

        stm.setString(1, pattern);
        stm.setString(2, pattern);

        ResultSet rs = stm.executeQuery();

        List<Notice> list = new ArrayList<>();

        while (rs.next()) {

            Date publishDate = rs.getDate("publishDate");

            Notice n = new Notice(
                    rs.getInt("notice_id"),
                    rs.getString("notice_title"),
                    rs.getString("notice_content"),
                    publishDate == null ? null : publishDate.toLocalDate(),
                    rs.getString("createdBy")
            );

            list.add(n);
        }

        return list;
    }

    public boolean save(Notice n) throws SQLException {

        String sql = "INSERT INTO notice (notice_title, notice_content, publishDate, createdBy) VALUES (?, ?, ?, ?)";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stm.setString(1, n.getTitle());
        stm.setString(2, n.getContent());

        if (n.getPublishDate() == null) {
            stm.setDate(3, null);
        } else {
            stm.setDate(3, Date.valueOf(n.getPublishDate()));
        }

        stm.setString(4, n.getCreatedBy());

        int result = stm.executeUpdate();

        if (result > 0) {
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                n.setNoticeId(rs.getInt(1));
            }
            return true;
        }

        return false;
    }

    public boolean update(Notice n) throws SQLException {

        String sql = "UPDATE notice SET notice_title=?, notice_content=?, publishDate=?, createdBy=? WHERE notice_id=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setString(1, n.getTitle());
        stm.setString(2, n.getContent());

        if (n.getPublishDate() == null) {
            stm.setDate(3, null);
        } else {
            stm.setDate(3, Date.valueOf(n.getPublishDate()));
        }

        stm.setString(4, n.getCreatedBy());
        stm.setInt(5, n.getNoticeId());

        return stm.executeUpdate() > 0;
    }

    public boolean deleteById(int id) throws SQLException {

        String sql = "DELETE FROM notice WHERE notice_id=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setInt(1, id);

        return stm.executeUpdate() > 0;
    }
}
