package com.example.java_lms_group_01.model;

public class Marks {
    private int studentId;
    private String subject;
    private double caMarks;
    private double finalMarks;

    public boolean isEligible() {
        return caMarks >= 40;
    }

    public double getTotal() {
        return (caMarks * 0.4) + (finalMarks * 0.6);
    }
}