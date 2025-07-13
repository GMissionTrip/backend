package com.gangchu.gangchutrip.route.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RouteRequestDto {
    private CoordinateDto origin;
    private CoordinateDto destination;
    private List<CoordinateDto> waypoints;
    private Map<Integer, Integer> orderConstraint;

    public KakaoRouteRequestDto toKakaoDto() {
        return toKakaoDtoWithWaypoints(this.waypoints);
    }

    public KakaoRouteRequestDto toKakaoDtoWithWaypoints(List<CoordinateDto> waypoints) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("origin과 destination은 필수입니다.");
        }
        return KakaoRouteRequestDto.builder()
                .origin(coordToMap(origin))
                .destination(coordToMap(destination))
                .waypoints(waypointsToMapList(waypoints == null ? new ArrayList<>() : waypoints))
                .priority("RECOMMEND")
                .carFuel("GASOLINE")
                .carHipass(false)
                .alternatives(false)
                .roadDetails(true)
                .summary(true)
                .build();
    }

    private Map<String, Object> coordToMap(CoordinateDto c) {
        if (c == null) throw new IllegalArgumentException("CoordinateDto는 null일 수 없습니다.");
        Map<String, Object> map = new HashMap<>();
        map.put("x", c.getX());
        map.put("y", c.getY());
        if (c.getName() != null) {
            map.put("name", c.getName());
        }
        return map;
    }

    private List<Map<String, Object>> waypointsToMapList(List<CoordinateDto> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list != null) {
            for (CoordinateDto c : list) {
                result.add(coordToMap(c));
            }
        }
        return result;
    }
}
