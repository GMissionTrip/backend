package com.gangchu.gangchutrip.mission.text.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReverseMissionRequestDto {
    private Long touristId;
    private Long routeId;
    private Long memberId;
    private String question;
    private String answer;
}