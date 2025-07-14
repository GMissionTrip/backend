package com.gangchu.gangchutrip.route.controller;

import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.route.dto.RouteRequestDto;
import com.gangchu.gangchutrip.route.service.RouteService;
import com.gangchu.gangchutrip.route.algorithm.TspSolver;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    private final TspSolver tspSolver;

    @PostMapping("/directions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDirections(@RequestBody RouteRequestDto request) {
        Map<String, Object> data = routeService.getDirections(request.toKakaoDto());
        return ApiResponseFactory.success(ResponseCode.ROUTE_FOUND, data);
    }

    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<Map<String, Object>>> optimizeRoute(@RequestBody RouteRequestDto request) {
        var optimized = tspSolver.solveTsp(request);
        Map<String, Object> data = routeService.getDirections(request.toKakaoDtoWithWaypoints(optimized));
        return ApiResponseFactory.success(ResponseCode.ROUTE_OPTIMIZED, data);
    }
}