package com.example.java_lms_group_01.model;

public class TechnicalOfficer extends User{
    private int deptId;
    private String position;
    private String labAssigned;
    private String shift;
    private String qualifications;

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLabAssigned() {
        return labAssigned;
    }

    public void setLabAssigned(String labAssigned) {
        this.labAssigned = labAssigned;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }
}
