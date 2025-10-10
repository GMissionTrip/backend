package com.gangchu.gangchutrip.mission.text.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemperatureMissionResponseDto {
    private Long id;
    private Integer temperature;
    private String temperatureLevel;
    private Long touristId;
    private Long routeId;
    private Long memberId;
    private boolean missionCompleted;
}
