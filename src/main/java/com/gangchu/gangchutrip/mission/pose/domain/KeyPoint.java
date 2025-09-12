package com.gangchu.gangchutrip.mission.pose.domain;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class KeyPoint {
    private double x;
    private double y;
    private double confidence;
}
