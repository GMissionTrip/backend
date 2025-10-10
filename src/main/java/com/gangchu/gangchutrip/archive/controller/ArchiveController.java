package com.gangchu.gangchutrip.archive.controller;


import com.gangchu.gangchutrip.archive.service.ArchiveService;
import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archives")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    @GetMapping
    public ResponseEntity<?> getArchives(@AuthenticationPrincipal MemberPrincipal principal) {
        return archiveService.getArchives(principal.getUsername());
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getArchivesDetail(@AuthenticationPrincipal MemberPrincipal principal, @RequestParam Long archiveId) {
        return archiveService.getArchivesDetail(principal.getUsername(), archiveId);
    }
}
