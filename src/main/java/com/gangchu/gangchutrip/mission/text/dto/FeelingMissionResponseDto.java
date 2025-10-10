package com.gangchu.gangchutrip.mission.text.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeelingMissionResponseDto {
    private Long id;
    private String content;
    private Long touristSpotId;
    private Long routeId;
    private Long memberId;
    private boolean missionCompleted;
}
