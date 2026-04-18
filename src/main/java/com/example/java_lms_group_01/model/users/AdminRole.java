package com.example.java_lms_group_01.model.users;

public interface AdminRole extends UserContract {
    String getRegistrationNo();

    void setRegistrationNo(String registrationNo);

    String getPassword();

    void setPassword(String password);
}
