package com.gangchu.gangchutrip.mission.text.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TemperatureMissionRequestDto {
    private Long touristId;
    private Long routeId;
    private Long memberId;
    private Integer temperature;
}
