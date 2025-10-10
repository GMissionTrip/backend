package com.gangchu.gangchutrip.travel.controller;

import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.travel.dto.TravelCreateRequestDTO;
import com.gangchu.gangchutrip.travel.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    @PostMapping
    public ResponseEntity<?> createTravel(@AuthenticationPrincipal MemberPrincipal principal, @RequestBody
        TravelCreateRequestDTO dto) {
        return travelService.createTravel(principal.getUsername(), dto);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTravel(@AuthenticationPrincipal MemberPrincipal principal) {
        return travelService.getAllTravel(principal.getUsername());
    }

    //query param -> path variable
    @GetMapping("/{travelId}")
    public ResponseEntity<?> getTravelDetail(@AuthenticationPrincipal MemberPrincipal principal, @PathVariable Long travelId) {
        return travelService.getTravelDetail(principal.getUsername(),travelId);
    }
}
