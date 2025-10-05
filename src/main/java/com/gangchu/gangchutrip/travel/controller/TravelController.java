package com.gangchu.gangchutrip.travel.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.travel.dto.CreateTravelRequestDto;
import com.gangchu.gangchutrip.travel.dto.TravelResponseDto;
import com.gangchu.gangchutrip.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
@Tag(name = "여행", description = "여행 관리 API")
public class TravelController {
    
    private final TravelService travelService;
    
    @PostMapping
    @Operation(summary = "여행 생성", description = "새로운 여행을 생성하고 미션을 자동으로 생성합니다.")
    public ResponseEntity<ApiResponse<TravelResponseDto>> createTravel(
            @RequestBody CreateTravelRequestDto request,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            return ApiResponseFactory.error(ResponseCode.UNAUTHORIZED);
        }
        
        Long memberId = Long.parseLong(principal.getUsername());
        TravelResponseDto travel = travelService.createTravel(request, memberId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, travel);
    }
    
    @PostMapping("/{travelId}/start")
    @Operation(summary = "여행 시작", description = "여행 상태를 IN_PROGRESS로 변경합니다.")
    public ResponseEntity<ApiResponse<TravelResponseDto>> startTravel(
            @PathVariable Long travelId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            return ApiResponseFactory.error(ResponseCode.UNAUTHORIZED);
        }
        
        Long memberId = Long.parseLong(principal.getUsername());
        TravelResponseDto travel = travelService.startTravel(travelId, memberId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, travel);
    }
    
    @PostMapping("/{travelId}/complete")
    @Operation(summary = "여행 완료", description = "여행 상태를 COMPLETED로 변경하고 아카이브를 생성합니다.")
    public ResponseEntity<ApiResponse<TravelResponseDto>> completeTravel(
            @PathVariable Long travelId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            return ApiResponseFactory.error(ResponseCode.UNAUTHORIZED);
        }
        
        Long memberId = Long.parseLong(principal.getUsername());
        TravelResponseDto travel = travelService.completeTravel(travelId, memberId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, travel);
    }
    
    @GetMapping
    @Operation(summary = "내 여행 목록 조회", description = "로그인한 사용자의 모든 여행을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TravelResponseDto>>> getMyTravels(
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            return ApiResponseFactory.error(ResponseCode.UNAUTHORIZED);
        }
        
        Long memberId = Long.parseLong(principal.getUsername());
        List<TravelResponseDto> travels = travelService.getMyTravels(memberId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, travels);
    }
    
    @GetMapping("/{travelId}")
    @Operation(summary = "여행 상세 조회", description = "특정 여행의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<TravelResponseDto>> getTravel(
            @PathVariable Long travelId) {
        TravelResponseDto travel = travelService.getTravel(travelId);
        return ApiResponseFactory.success(ResponseCode.SUCCESS, travel);
    }
}
