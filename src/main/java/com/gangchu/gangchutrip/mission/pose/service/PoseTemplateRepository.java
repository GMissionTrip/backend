package com.gangchu.gangchutrip.mission.pose.service;


import com.gangchu.gangchutrip.mission.pose.domain.Joint;
import com.gangchu.gangchutrip.mission.pose.domain.PoseTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PoseTemplateRepository {

    private final Map<String, PoseTemplate> store = new HashMap<>();

    public PoseTemplateRepository() {
        // DEMO 템플릿 등록 (운영에서 DB/MyBatis 연동 권장)
        PoseTemplate t = new PoseTemplate();
        t.setId("mission_pose_001");
        Map<Joint, Double> angles = new EnumMap<>(Joint.class);
        angles.put(Joint.LEFT_ELBOW, 60.0); angles.put(Joint.RIGHT_ELBOW, 160.0);
        angles.put(Joint.LEFT_KNEE, 175.0); angles.put(Joint.RIGHT_KNEE, 175.0);
        angles.put(Joint.LEFT_SHOULDER, 90.0); angles.put(Joint.RIGHT_SHOULDER, 20.0);
        angles.put(Joint.LEFT_HIP, 170.0); angles.put(Joint.RIGHT_HIP, 170.0);
        t.setJointAnglesDeg(angles);
        t.setCriticalJoints(Set.of(Joint.LEFT_ELBOW, Joint.RIGHT_ELBOW, Joint.LEFT_KNEE, Joint.RIGHT_KNEE));
        Map<String, Double> tol = new HashMap<>(); tol.put("default", 20.0); tol.put("KNEE", 15.0); tol.put("ELBOW", 18.0);
        t.setTolerancesDeg(tol);
        t.setLengthRatioTolerance(0.15); t.setVectorDirToleranceDeg(20.0);
        t.setPassScoreThreshold(80.0); t.setCoverageThreshold(0.85); t.setCriticalMinPerJointScore(0.7);
        store.put(t.getId(), t);
    }

    public PoseTemplate getById(String id) { return store.get(id); }
    public void put(PoseTemplate t) { store.put(t.getId(), t); }
}
