package com.gangchu.gangchutrip.mission.voice.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoiceMissionRequestDto {
    private Long memberId;
    private Long touristId;
    private Long routeId;
    private String context;
    private MultipartFile voiceFile;
}