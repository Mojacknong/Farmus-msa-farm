package com.example.farmusfarm.common;

import java.time.LocalDate;

public class Utils {

    public static int compareLocalDate(LocalDate from, LocalDate to) {
        return from.compareTo(to);
    }
}
