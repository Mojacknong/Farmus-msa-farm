package com.example.farmusfarm.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.example.farmusfarm.global.response.SuccessMessage.SUCCESS;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"code", "message", "data"})
public class BaseResponseDto<T> {
    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> BaseResponseDto of(SuccessMessage successMessage, T data){

        return BaseResponseDto.builder()
                .code(successMessage.getCode())
                .message(successMessage.getMessage())
                .data(data)
                .build();
    }

    public static <T> BaseResponseDto of(ErrorMessage errorMessage){

        return BaseResponseDto.builder()
                .code(errorMessage.getCode())
                .message(errorMessage.getMessage())
                .build();
    }
}