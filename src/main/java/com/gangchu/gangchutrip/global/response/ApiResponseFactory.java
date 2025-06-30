package com.gangchu.gangchutrip.global.response;

import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {
    public static <T> ResponseEntity<ApiResponse<T>> success(ResponseCode code, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.success(code, data));
    }

    public static ResponseEntity<ApiResponse<Void>> success(ResponseCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.success(code));
    }
}