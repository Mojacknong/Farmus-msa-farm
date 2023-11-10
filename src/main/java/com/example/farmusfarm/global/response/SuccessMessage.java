package com.example.farmusfarm.global.response;

import lombok.Getter;

@Getter
public enum SuccessMessage {
    SUCCESS(200, "요청에 성공하였습니다.");

    private final int code;
    private final String message;

    SuccessMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

}