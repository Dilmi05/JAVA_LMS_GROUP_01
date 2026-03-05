package com.example.java_lms_group_01.model;

public class Timetable {
    private int timetableId;
    private int departmentId;
    private int semester;
    private String academicYear;

    public Timetable() {
    }

    public Timetable(int timetableId, int departmentId, int semester, String academicYear) {
        this.timetableId = timetableId;
        this.departmentId = departmentId;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}
