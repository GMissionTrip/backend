package com.gangchu.gangchutrip.mission.voice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoiceMissionResponseDto {
    private Long id;
    private String resultUrl;
    private String context;
    private Long touristId;
    private Long routeId;
    private Long memberId;
    private boolean missionCompleted;
}