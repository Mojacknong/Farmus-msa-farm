package com.example.farmusfarm.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorMessage {

    INTERVAL_SERVER_ERROR(1001,  "요청을 처리하는 과정에서 서버가 예상하지 못한 오류가 발생하였습니다."),
    REFRESH_NOTIFICATION_ERROR(4017,  "Refresh Token 인증 오류");



    private final int code;
    private final String message;

    ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}