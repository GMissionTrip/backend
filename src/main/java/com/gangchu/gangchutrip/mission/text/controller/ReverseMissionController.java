package com.gangchu.gangchutrip.mission.text.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;

import com.gangchu.gangchutrip.mission.text.dto.ReverseMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.dto.ReverseMissionResponseDto;
import com.gangchu.gangchutrip.mission.text.service.ReverseMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions/reverse")
@RequiredArgsConstructor
public class ReverseMissionController {

    private final ReverseMissionService reverseMissionService;


    @GetMapping
    public ResponseEntity<ApiResponse<String>> getRandomQuestion() {
        return reverseMissionService.getRandomQuestion();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReverseMissionResponseDto>> submitReverseMission(
            @RequestBody ReverseMissionRequestDto requestDto) {
        return reverseMissionService.submitReverseMission(requestDto);
    }
}