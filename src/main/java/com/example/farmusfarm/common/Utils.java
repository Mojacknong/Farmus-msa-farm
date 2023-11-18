package com.example.farmusfarm.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Utils {

    public static int compareLocalDate(LocalDate from, LocalDate to) {
        return - (int) ChronoUnit.DAYS.between(from, to);
    }

    // mm/dd/yy, hh:mm PM -> mm/dd hh:mm
    public static String dateTimeFormat(String dateTime) {
        String[] dateTimeArr = dateTime.split(" ");
        String[] dateArr = dateTimeArr[0].split("/");
        String[] timeArr = dateTimeArr[1].split(":");
        String ampm = dateTimeArr[2];

        String month = dateArr[0];
        String day = dateArr[1];
        String year = dateArr[2];

        String hour = timeArr[0];
        String minute = timeArr[1];

        if (ampm.equals("PM")) {
            hour = String.valueOf(Integer.parseInt(hour) + 12);
        }

        return month + "/" + day + " " + hour + ":" + minute;
    }
}
