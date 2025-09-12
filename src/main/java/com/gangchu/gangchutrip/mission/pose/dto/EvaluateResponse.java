package com.gangchu.gangchutrip.mission.pose.dto;

import com.gangchu.gangchutrip.mission.pose.domain.*;
import lombok.*;
import java.util.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class EvaluateResponse {

    private boolean success;
    private double score; // 0~100
    private Map<Joint, Double> perJointScore; // 0~1
    private double coverage; // 0~1 (감지된 유효 키포인트 비율)
    private List<String> notes;
}
