package com.gangchu.gangchutrip.mission.text.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FeelingMissionRequestDto {
    private Long touristSpotId;
    private Long routeId;
    private Long memberId;
    private String content;
}
