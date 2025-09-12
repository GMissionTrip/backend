package com.gangchu.gangchutrip.member.controller;


import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal MemberPrincipal principal) {
        return memberService.getMemberInfo(principal.getUsername());
    }

    @PatchMapping
    public ResponseEntity<?> updateMemberInfo(@AuthenticationPrincipal MemberPrincipal principal, @RequestBody String nickname, @RequestBody String profileImageUrl) {
        return memberService.updateMemberInfo(principal.getUsername(), nickname, profileImageUrl);
    }

    @GetMapping("/point")
    public ResponseEntity<?> getMemberPoint(@AuthenticationPrincipal MemberPrincipal principal) {
        return memberService.getMemberPoint(principal.getUsername());
    }

    @GetMapping("/point/detail")
    public ResponseEntity<?> getMemberPointDetail(@AuthenticationPrincipal MemberPrincipal principal) {
        return memberService.getMemberPointDetail(principal.getUsername());
    }
}
