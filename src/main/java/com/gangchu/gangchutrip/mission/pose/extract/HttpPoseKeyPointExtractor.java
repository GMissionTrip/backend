package com.gangchu.gangchutrip.mission.pose.extract;

import com.gangchu.gangchutrip.mission.pose.domain.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HttpPoseKeyPointExtractor implements PoseKeyPointExtractor{

    private final WebClient poseWebClient; // configured with baseUrl

    public HttpPoseKeyPointExtractor(WebClient poseWebClient) {
        this.poseWebClient = poseWebClient;
    }

    @Override
    public Map<Joint, KeyPoint> extract(MultipartFile image) throws IOException {
        byte[] bytes = image.getBytes();
        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return Optional.ofNullable(image.getOriginalFilename()).orElse("upload.jpg");
            }
        }).contentType(MediaType.IMAGE_JPEG);

        InferResponse resp = poseWebClient.post()
            .uri("/infer") // configurable path on the Python service
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(mb.build()))
            .retrieve()
            .bodyToMono(InferResponse.class)
            .block();

        if (resp == null || resp.keypoints == null)
            throw new IllegalStateException("Pose inference failed or empty response");
        if (!"coco17".equalsIgnoreCase(resp.schema)) {
            throw new IllegalStateException("Unexpected schema from pose service: " + resp.schema);
        }
        Map<Joint, KeyPoint> out = new EnumMap<>(Joint.class);
        for (Kp k : resp.keypoints) {
            try {
                Joint j = Joint.valueOf(k.name.toUpperCase());
                out.put(j, KeyPoint.builder().x(k.x).y(k.y).confidence(k.confidence).build());
            } catch (IllegalArgumentException iae) {
                // unknown joint name from the model, ignore
                // log.warn("Unknown joint name from pose model: {}", k.name);
            }
        }
        return out;
    }
    public static class InferResponse {
        public String schema; // "coco17"
        public List<Kp> keypoints;
    }
    public static class Kp {
        public String name; public double x; public double y; public double confidence;
    }
}
