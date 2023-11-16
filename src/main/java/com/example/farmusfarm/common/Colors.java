package com.example.farmusfarm.common;

import lombok.Getter;

// green, blue, purple and their hex codes
@Getter
public enum Colors {
    GREEN("#DCFFD4"),
    PURPLE("#EEE3FF"),
    RED("#FFE3ED");

    private final String hexCode;

    Colors(String hexCode) {
        this.hexCode = hexCode;
    }
}
