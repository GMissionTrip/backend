package com.gangchu.gangchutrip.route.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class KakaoRouteResponseDto {
    @JsonProperty("trans_id")
    private String transId;
    private List<RouteDto> routes;

    @Getter
    public static class RouteDto {
        @JsonProperty("result_code")
        private int resultCode;
        @JsonProperty("result_msg")
        private String resultMsg;
        private SummaryDto summary;
        private List<SectionDto> sections;
    }

    @Getter
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

    @Getter
    public static class PlaceDto {
        private String name;
        private double x;
        private double y;
    }

    @Getter
    public static class BoundDto {
        @JsonProperty("min_x")
        private double minX;
        @JsonProperty("min_y")
        private double minY;
        @JsonProperty("max_x")
        private double maxX;
        @JsonProperty("max_y")
        private double maxY;
    }

    @Getter
    public static class FareDto {
        private int taxi;
        private int toll;
    }

    @Getter
    public static class SectionDto {
        private int distance;
        private int duration;
    }
}
