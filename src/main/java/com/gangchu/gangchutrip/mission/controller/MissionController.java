package com.gangchu.gangchutrip.mission.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.mission.dto.MissionResponseDto;
import com.gangchu.gangchutrip.mission.dto.SubmitMissionRequestDto;
import com.gangchu.gangchutrip.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Tag(name = "미션", description = "여행 미션 관련 API")
public class MissionController {
    
    private final MissionService missionService;
    
    @GetMapping("/travel/{travelId}")
    @Operation(summary = "여행의 미션 목록 조회", description = "특정 여행의 모든 미션을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MissionResponseDto>>> getTravelMissions(
            @PathVariable Long travelId) {
        List<MissionResponseDto> missions = missionService.getTravelMissions(travelId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, missions);
    }
    
    @GetMapping("/{missionId}")
    @Operation(summary = "미션 상세 조회", description = "특정 미션의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MissionResponseDto>> getMission(
            @PathVariable Long missionId) {
        MissionResponseDto mission = missionService.getMission(missionId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, mission);
    }
    
    @PostMapping("/{missionId}/submit")
    @Operation(summary = "미션 제출", description = "미션을 완료하고 제출합니다.")
    public ResponseEntity<ApiResponse<MissionResponseDto>> submitMission(
            @PathVariable Long missionId,
            @RequestBody SubmitMissionRequestDto request,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            return ApiResponseFactory.error(ResponseCode.UNAUTHORIZED);
        }
        
        Long memberId = Long.parseLong(principal.getUsername());
        MissionResponseDto mission = missionService.submitMission(missionId, request, memberId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, mission);
    }
    
    @PostMapping("/{missionId}/unlock")
    @Operation(summary = "미션 잠금 해제", description = "잠긴 미션을 해제합니다.")
    public ResponseEntity<ApiResponse<MissionResponseDto>> unlockMission(
            @PathVariable Long missionId) {
        MissionResponseDto mission = missionService.unlockMission(missionId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, mission);
    }
}

