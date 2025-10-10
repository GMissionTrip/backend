package com.gangchu.gangchutrip.mission.text.controller;

import com.gangchu.gangchutrip.mission.text.dto.FeelingMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.service.FeelingMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions/feeling")
@RequiredArgsConstructor
public class FeelingMissionController {

    private final FeelingMissionService feelingMissionService;

    @PostMapping
    public ResponseEntity<?> submitFeelingMission(
            @RequestBody FeelingMissionRequestDto requestDto)  {
        return feelingMissionService.submitFeelingMission(requestDto);
    }
}
