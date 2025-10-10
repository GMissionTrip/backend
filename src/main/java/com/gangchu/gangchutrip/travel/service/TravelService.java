package com.gangchu.gangchutrip.travel.service;


import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TravelService {

    public ResponseEntity<?> createTravel() {
        return ApiResponseFactory.success(ResponseCode.OK);
    }
}
