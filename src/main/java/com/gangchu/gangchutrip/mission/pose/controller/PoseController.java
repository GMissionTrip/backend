package com.gangchu.gangchutrip.mission.pose.controller;

import com.gangchu.gangchutrip.mission.pose.extract.PoseKeyPointExtractor;
import java.io.IOException;
import java.util.*;
import com.gangchu.gangchutrip.mission.pose.domain.*;
import com.gangchu.gangchutrip.mission.pose.dto.*;
import com.gangchu.gangchutrip.mission.pose.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pose")
@RequiredArgsConstructor
public class PoseController {
    private final PoseEvaluationService evaluator;
    private final PoseKeyPointExtractor extractor;

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateResponse> evaluate(@RequestBody EvaluateRequest req) {
        PoseEvaluationService.EvaluateResult r = evaluator.evaluate(req.getMissionPoseId(), req.getUserKeypoints(), req.isAllowMirror());
        EvaluateResponse resp = EvaluateResponse.builder()
            .success(r.success)
            .score(r.score)
            .perJointScore(r.perJoint)
            .coverage(r.coverage)
            .notes(r.notes)
            .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/evaluate-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvaluateResponse> evaluateImage(
        @RequestParam("missionPoseId") String missionPoseId,
        @RequestParam(value = "allowMirror", required = false, defaultValue = "true") boolean allowMirror,
        @RequestPart("file") MultipartFile image
    ) throws IOException {
        Map<Joint, KeyPoint> userKeypoints = extractor.extract(image);
        PoseEvaluationService.EvaluateResult r = evaluator.evaluate(missionPoseId, userKeypoints, allowMirror);
        EvaluateResponse resp = EvaluateResponse.builder()
            .success(r.success)
            .score(r.score)
            .perJointScore(r.perJoint)
            .coverage(r.coverage)
            .notes(r.notes)
            .build();
        return ResponseEntity.ok(resp);
    }
}
