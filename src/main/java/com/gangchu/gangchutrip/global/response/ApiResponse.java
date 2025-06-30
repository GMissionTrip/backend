package com.gangchu.gangchutrip.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(ResponseCode code, T data) {
        return new ApiResponse<>(code.getHttpStatusCode(), true, code.getMessage(), data);
    }

    public static ApiResponse<Void> success(ResponseCode code) {
        return new ApiResponse<>(code.getHttpStatusCode(), true, code.getMessage(), null);
    }

    public static ApiResponse<Void> error(ResponseCode code) {
        return new ApiResponse<>(code.getHttpStatusCode(), false, code.getMessage(), null);
    }

    public static ApiResponse<Void> error(int status, String message) {
        return new ApiResponse<>(status, false, message, null);
    }
}
