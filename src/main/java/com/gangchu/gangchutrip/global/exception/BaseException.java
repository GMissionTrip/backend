package com.gangchu.gangchutrip.global.exception;

import com.gangchu.gangchutrip.global.response.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    private final ResponseCode responseCode;
    @Override
    public String getMessage() {
        return responseCode.getMessage();
    }
}
