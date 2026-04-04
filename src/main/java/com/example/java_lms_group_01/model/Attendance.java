package com.example.java_lms_group_01.model;

public class Attendance {
    private int studentId;
    private String subject;
    private int totalSessions;
    private int attendedSessions;
    private int medicalSessions;

    public double getPercentage() {
        return ((double)(attendedSessions + medicalSessions) / totalSessions) * 100;
    }
}
