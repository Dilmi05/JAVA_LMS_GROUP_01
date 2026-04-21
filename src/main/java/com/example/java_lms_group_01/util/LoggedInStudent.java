package com.example.java_lms_group_01.util;

// maintains a class for current Logged User [ get registrationNo for operations ]
public class LoggedInStudent {

    private static String registrationNo;

    private LoggedInStudent() {
    }

    public static void setRegistrationNo(String regNo) {
        registrationNo = regNo;
    }

    public static String getRegistrationNo() {
        return registrationNo;
    }

    public static void clear() {
        registrationNo = null;
    }
}
