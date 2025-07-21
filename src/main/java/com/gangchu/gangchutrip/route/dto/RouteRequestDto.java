package com.gangchu.gangchutrip.route.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
public class RouteRequestDto {
    private CoordinateDto origin;
    private CoordinateDto destination;
    private List<CoordinateDto> waypoints;
    private Map<Integer, Integer> orderConstraint;
}
