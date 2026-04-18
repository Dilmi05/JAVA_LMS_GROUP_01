package com.example.java_lms_group_01.model.users;

public enum UserRole {
    ADMIN("Admin"),
    LECTURER("Lecturer"),
    STUDENT("Student"),
    TECHNICAL_OFFICER("TechnicalOfficer");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
