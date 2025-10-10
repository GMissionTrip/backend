package com.gangchu.gangchutrip.mission.text.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReverseMissionResponseDto {
    private Long id;
    private String question;
    private String answer;
    private Long touristId;
    private Long routeId;
    private Long memberId;
    private boolean missionCompleted;
}