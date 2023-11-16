package com.example.farmusfarm.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Utils {

    public static int compareLocalDate(LocalDate from, LocalDate to) {
        return - (int) ChronoUnit.DAYS.between(from, to);
    }
}
