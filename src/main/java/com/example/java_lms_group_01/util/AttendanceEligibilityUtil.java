package com.example.java_lms_group_01.util;

//To check the Attendance Eligibility
public final class AttendanceEligibilityUtil {

    public static final double MIN_ELIGIBILITY_PERCENTAGE = 80.0;

    private AttendanceEligibilityUtil() {
    }

    public static double calculatePercentage(int eligibleSessions, int totalSessions) {
        if (totalSessions <= 0) {
            return 0.0;
        }
        return (eligibleSessions * 100.0) / totalSessions;
    }
}