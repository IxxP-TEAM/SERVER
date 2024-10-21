package com.ip.api.apiPayload.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private String code;
    private T data;

    private ApiResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.code = code.getCode();
    }

    private ApiResponse(final ErrorCode code, final String message) {
        this.message = message;
        this.code = code.getCode();
    }

    private ApiResponse(final String message, final int status, final T data) {
        this.message = message;
        this.code = "200";  // 성공 코드는 일반적으로 200으로 설정
        this.data = data;
    }

    public static ApiResponse of(final ErrorCode code) {
        return new ApiResponse(code);
    }

    public static ApiResponse of(final ErrorCode code, final String message) {
        return new ApiResponse(code, message);
    }
    public static <T> ApiResponse<T> of(final T data) {
        return new ApiResponse<>("Success", 200, data);
    }
}
