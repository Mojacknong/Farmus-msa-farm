package com.example.farmusfarm.global.exception;

import com.example.farmusfarm.global.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInputFieldException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException : {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        FieldError mainError = e.getFieldErrors().get(0);
        String[] errorInfo = Objects.requireNonNull(mainError.getDefaultMessage()).split(":");
        String message = errorInfo[0];
        return ResponseEntity.badRequest().body(new ErrorResponse(BAD_REQUEST, message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        log.error("BindException : {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        FieldError mainError = e.getFieldErrors().get(0);
        String[] errorInfo = Objects.requireNonNull(mainError.getDefaultMessage()).split(":");
        String message = errorInfo[0];
        return ResponseEntity.badRequest().body(new ErrorResponse(BAD_REQUEST, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchExceptionException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException : {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedExceptionException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("HttpRequestMethodNotSupportedException : {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(Exception e, HttpServletRequest request) {
        log.error("UnhandledException: {} {} errMessage={}\n{}",
                request.getMethod(),
                request.getRequestURI(),
                e.getStackTrace(),
                e.getMessage()
        );
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버와의 접속이 원활하지 않습니다."));
    }
}
