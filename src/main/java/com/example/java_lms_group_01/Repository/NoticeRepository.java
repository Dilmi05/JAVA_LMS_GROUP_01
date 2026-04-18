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

        String sql = "SELECT notice_id, notice_title, notice_content, publishDate, createdBy " +
                "FROM notice ORDER BY publishDate DESC, notice_id DESC";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();

        List<Notice> list = new ArrayList<>();

        while (rs.next()) {
            list.add(mapRow(rs));
        }

        return list;
    }

    public List<Notice> findByKeyword(String keyword) throws SQLException {

        String sql = "SELECT notice_id, notice_title, notice_content, publishDate, createdBy FROM notice WHERE 1=1";

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND (notice_title LIKE ? OR notice_content LIKE ?)";

            String pattern = "%" + keyword + "%";

            params.add(pattern);
            params.add(pattern);
        }

        sql += " ORDER BY publishDate DESC, notice_id DESC";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            stm.setString(i + 1, params.get(i));
        }

        ResultSet rs = stm.executeQuery();

        List<Notice> list = new ArrayList<>();

        while (rs.next()) {
            list.add(mapRow(rs));
        }

        return list;
    }

    public boolean save(Notice n) throws SQLException {

        String sql = "INSERT INTO notice (notice_title, notice_content, publishDate, createdBy) VALUES (?, ?, ?, ?)";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        setData(stm, n, false);

        boolean saved = stm.executeUpdate() > 0;

        if (saved) {
            ResultSet rs = stm.getGeneratedKeys();

            if (rs.next()) {
                n.setNoticeId(rs.getInt(1));
            }
        }

        return saved;
    }

    public boolean update(Notice n) throws SQLException {

        String sql = "UPDATE notice SET notice_title=?, notice_content=?, publishDate=?, createdBy=? WHERE notice_id=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        setData(stm, n, true);

        return stm.executeUpdate() > 0;
    }

    public boolean deleteById(int id) throws SQLException {

        String sql = "DELETE FROM notice WHERE notice_id=?";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);

        stm.setInt(1, id);

        return stm.executeUpdate() > 0;
    }

    private void setData(PreparedStatement stm, Notice n, boolean update) throws SQLException {

        if (!update) {
            stm.setString(1, n.getTitle());
            stm.setString(2, n.getContent());
            stm.setDate(3, n.getPublishDate() == null ? null : Date.valueOf(n.getPublishDate()));
            stm.setString(4, n.getCreatedBy());
        } else {
            stm.setString(1, n.getTitle());
            stm.setString(2, n.getContent());
            stm.setDate(3, n.getPublishDate() == null ? null : Date.valueOf(n.getPublishDate()));
            stm.setString(4, n.getCreatedBy());
            stm.setInt(5, n.getNoticeId());
        }
    }

    private Notice mapRow(ResultSet rs) throws SQLException {

        Date publishDate = rs.getDate("publishDate");

        return new Notice(
                rs.getInt("notice_id"),
                rs.getString("notice_title"),
                rs.getString("notice_content"),
                publishDate == null ? null : publishDate.toLocalDate(),
                rs.getString("createdBy")
        );
    }
}
