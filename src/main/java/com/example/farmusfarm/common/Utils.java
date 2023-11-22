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

    // mm/dd/yy, hh:mm PM -> yyyy년 mm월 dd일
    public static String dateTimeToDateFormat(String dateTime) {
        String[] dateTimeArr = dateTime.split(" ");
        String[] dateArr = dateTimeArr[0].split("/");

        String month = dateArr[0];
        String day = dateArr[1];
        String year = dateArr[2].substring(0, 2);


        return "20" + year + "년 " + month + "월 " + day + "일";
    }
}
