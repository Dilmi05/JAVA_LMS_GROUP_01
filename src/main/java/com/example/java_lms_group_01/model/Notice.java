package com.example.java_lms_group_01.model;

import java.time.LocalDate;

public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private LocalDate publishDate;
    private int createdByAdminId;

    public Notice() {
    }

    public Notice(int noticeId, String title, String content, LocalDate publishDate, int createdByAdminId) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.publishDate = publishDate;
        this.createdByAdminId = createdByAdminId;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public int getCreatedByAdminId() {
        return createdByAdminId;
    }

    public void setCreatedByAdminId(int createdByAdminId) {
        this.createdByAdminId = createdByAdminId;
    }
}
