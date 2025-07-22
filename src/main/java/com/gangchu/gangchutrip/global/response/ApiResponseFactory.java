package com.gangchu.gangchutrip.global.response;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

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

    public static <T> ResponseEntity<ApiResponse<T>> error(ResponseCode code, String message) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.error(code, message));

    public static ResponseEntity<Void> success(ResponseCode code, URI uri) {
        return ResponseEntity
                .status(code.getStatus())
                .location(uri)
                .build();
    }

    public static ResponseEntity<String> success(ResponseCode code, String html) {
        return ResponseEntity
                .status(code.getStatus())
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

}