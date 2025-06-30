package com.gangchu.gangchutrip.global.exception;


import com.gangchu.gangchutrip.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e){
        return ResponseEntity
                .status(e.getResponseCode().getStatus())
                .body(ApiResponse.error(e.getResponseCode()));
    }
}
