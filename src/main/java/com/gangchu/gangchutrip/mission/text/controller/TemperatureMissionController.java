package com.gangchu.gangchutrip.mission.text.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;

import com.gangchu.gangchutrip.mission.text.dto.TemperatureMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.dto.TemperatureMissionResponseDto;
import com.gangchu.gangchutrip.mission.text.service.TemperatureMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions/temperature")
@RequiredArgsConstructor
public class TemperatureMissionController {

    private final TemperatureMissionService temperatureMissionService;


    @PostMapping
    public ResponseEntity<ApiResponse<TemperatureMissionResponseDto>> submitTemperatureMission(
            @RequestBody TemperatureMissionRequestDto requestDto) {
        return temperatureMissionService.submitTemperatureMission(requestDto);
    }

}