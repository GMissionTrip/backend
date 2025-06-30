package com.gangchu.gangchutrip.global.exception;

import com.gangchu.gangchutrip.global.response.ResponseCode;

public class JwtException extends BaseException {
    public JwtException(ResponseCode code) {
        super(code);
    }
}
