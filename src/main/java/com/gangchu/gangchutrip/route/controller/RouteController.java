package com.gangchu.gangchutrip.route.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.route.dto.RouteRequestDto;
import com.gangchu.gangchutrip.route.service.RouteService;
import com.gangchu.gangchutrip.route.algorithm.TspSolver;
import com.gangchu.gangchutrip.route.dto.KakaoRouteResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Route", description = "다중경유지 길찾기/최적화 API")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    private final TspSolver tspSolver;

    @Operation(summary = "다중경유지 길찾기", description = "경유지를 포함한 경로를 반환합니다.")
    @PostMapping("/directions")
    public ResponseEntity<ApiResponse<KakaoRouteResponseDto>> getDirections(@RequestBody RouteRequestDto request) {
        return routeService.getDirections(request);
    }

    @Operation(summary = "다중경유지 최적화", description = "경유지 순서를 최적화한 경로를 반환합니다.")
    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<KakaoRouteResponseDto>> optimizeRoute(@RequestBody RouteRequestDto request) {
        return routeService.getOptimizedDirections(request, tspSolver.solveTsp(request));
    }
}
