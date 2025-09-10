package com.gangchu.gangchutrip.mission.pose.domain;

import lombok.*;
import java.util.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PoseTemplate {
    private String id; // e.g., "mission_pose_001"
    private Map<Joint, KeyPoint> referenceKeypoints; // optional (for visual overlay)
    private Map<Joint, Double> jointAnglesDeg; // elbow/knee/hip/shoulder etc.
    private Map<String, Double> weights; // e.g., UPPER_BODY=0.5, LOWER_BODY=0.3, HEAD_NECK=0.2
    private Set<Joint> criticalJoints; // must pass minimal scores
    private Map<String, Double> tolerancesDeg; // default=20, ELBOW=18, KNEE=15, SHOULDER_ABD=20
    private Double lengthRatioTolerance; // e.g., 0.15
    private Double vectorDirToleranceDeg; // e.g., 20
    // thresholds (can be app.yml, exposed here for simplicity)
    private Double passScoreThreshold; // e.g., 80.0
    private Double coverageThreshold; // e.g., 0.85
    private Double criticalMinPerJointScore; // e.g., 0.7
}
