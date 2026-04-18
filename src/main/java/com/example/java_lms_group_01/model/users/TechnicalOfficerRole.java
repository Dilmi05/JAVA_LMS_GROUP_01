package com.example.java_lms_group_01.model.users;

public interface TechnicalOfficerRole extends UserContract {
    String getRegistrationNo();

    void setRegistrationNo(String registrationNo);

    String getPassword();

    void setPassword(String password);
}
