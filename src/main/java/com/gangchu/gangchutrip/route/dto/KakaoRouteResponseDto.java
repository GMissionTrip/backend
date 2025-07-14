package com.gangchu.gangchutrip.route.dto;

import lombok.Data;
import java.util.List;

@Data
public class KakaoRouteResponseDto {
    private String trans_id;
    private List<RouteDto> routes;

    @Data
    public static class RouteDto {
        private int result_code;
        private String result_msg;
        private SummaryDto summary;
        private List<SectionDto> sections;
    }

    @Data
    public static class SummaryDto {
        private PlaceDto origin;
        private PlaceDto destination;
        private List<PlaceDto> waypoints;
        private String priority;
        private BoundDto bound;
        private FareDto fare;
        private int distance;
        private int duration;
    }

    @Data
    public static class PlaceDto {
        private String name;
        private double x;
        private double y;
    }

    @Data
    public static class BoundDto {
        private double min_x;
        private double min_y;
        private double max_x;
        private double max_y;
    }

    @Data
    public static class FareDto {
        private int taxi;
        private int toll;
    }

    @Data
    public static class SectionDto {
        private int distance;
        private int duration;
    }
}

