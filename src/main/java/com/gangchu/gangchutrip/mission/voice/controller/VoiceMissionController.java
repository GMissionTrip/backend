package com.gangchu.gangchutrip.mission.voice.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;

import com.gangchu.gangchutrip.mission.voice.dto.VoiceMissionRequestDto;
import com.gangchu.gangchutrip.mission.voice.dto.VoiceMissionResponseDto;
import com.gangchu.gangchutrip.mission.voice.service.VoiceMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions/voice")
public class VoiceMissionController {

    private final VoiceMissionService voiceMissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<VoiceMissionResponseDto>> submitVoiceMission(
            @ModelAttribute VoiceMissionRequestDto requestDto) {
        return voiceMissionService.submitVoiceMission(requestDto);
    }
}