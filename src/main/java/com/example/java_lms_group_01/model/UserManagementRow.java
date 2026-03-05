package com.example.java_lms_group_01.model;

import java.time.LocalDate;

public class UserManagementRow {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;

    private String role;
    private String registrationNo;
    private Integer deptId;
    private Integer batchId;
    private String status;
    private String position;
    private LocalDate dateOfAppointment;
    private String accessLevel;
    private LocalDate dateOfJoining;
    private String labAssigned;
    private String shift;
    private String qualifications;

    public UserManagementRow(int userId, String firstName, String lastName, String email, String address,
                             String phoneNumber, LocalDate dateOfBirth, String gender, String role,
                             String registrationNo, Integer deptId, Integer batchId, String status,
                             String position, LocalDate dateOfAppointment, String accessLevel,
                             LocalDate dateOfJoining, String labAssigned, String shift, String qualifications) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
        this.registrationNo = registrationNo;
        this.deptId = deptId;
        this.batchId = batchId;
        this.status = status;
        this.position = position;
        this.dateOfAppointment = dateOfAppointment;
        this.accessLevel = accessLevel;
        this.dateOfJoining = dateOfJoining;
        this.labAssigned = labAssigned;
        this.shift = shift;
        this.qualifications = qualifications;
    }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getRole() { return role; }
    public String getRegistrationNo() { return registrationNo; }
    public Integer getDeptId() { return deptId; }
    public Integer getBatchId() { return batchId; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public LocalDate getDateOfAppointment() { return dateOfAppointment; }
    public String getAccessLevel() { return accessLevel; }
    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public String getLabAssigned() { return labAssigned; }
    public String getShift() { return shift; }
    public String getQualifications() { return qualifications; }
}
