package com.gangchu.gangchutrip.route.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class KakaoRouteRequestDto {
    private final Map<String, Object> origin;
    private final Map<String, Object> destination;
    private final List<Map<String, Object>> waypoints;
    private final String priority;
    @JsonProperty("car_fuel")
    private final String carFuel;
    @JsonProperty("car_hipass")
    private final boolean carHipass;
    private final boolean alternatives;
    @JsonProperty("road_details")
    private final boolean roadDetails;
    private final boolean summary;
}