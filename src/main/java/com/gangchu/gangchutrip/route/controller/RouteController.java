package com.gangchu.gangchutrip.route.controller;

import com.gangchu.gangchutrip.route.dto.RouteRequestDto;
import com.gangchu.gangchutrip.route.service.RouteService;
import com.gangchu.gangchutrip.route.algorithm.TspSolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    private final TspSolver tspSolver;

    @PostMapping("/directions")
    public ResponseEntity<String> getDirections(@RequestBody RouteRequestDto request) {
        return ResponseEntity.ok(routeService.getDirections(request.toKakaoDto()));
    }

    @PostMapping("/optimize")
    public ResponseEntity<String> optimizeRoute(@RequestBody RouteRequestDto request) {
        var optimized = tspSolver.solveTsp(request);
        return ResponseEntity.ok(routeService.getDirections(request.toKakaoDtoWithWaypoints(optimized)));
    }
}