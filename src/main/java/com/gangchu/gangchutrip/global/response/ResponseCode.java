package com.gangchu.gangchutrip.global.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public enum ResponseCode {

    //여기에 각자 추가해서 사용하는게 좋지 않을까요..?
    //204 no content
    NO_DATA(HttpStatus.NO_CONTENT, "NO DATA"),
    //400 validation
    OK(HttpStatus.OK, "OK"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD REQUEST"),
    //ex
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "올바르지 않은 인자입니다"),

    //401 authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),

    //403 forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),

    //404 not found
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND"),

    //500 internal server error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB ERROR");


    private final HttpStatus status;
    private final String message;

    public int getHttpStatusCode() { return status.value(); }
}
