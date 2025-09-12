package com.gangchu.gangchutrip.mission.pose.config;

import com.gangchu.gangchutrip.mission.pose.extract.PoseKeyPointExtractor;
import com.gangchu.gangchutrip.mission.pose.extract.HttpPoseKeyPointExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class PoseExtractorConfig {

    @Bean
    @Primary
    public WebClient poseWebClient(@Value("${pose.http.base-url:http://localhost:8000}") String baseUrl,
        @Value("${pose.http.timeout-ms:5000}") long timeoutMs) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        // (필요 시) .clientConnector(ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofMillis(timeoutMs))))
    }

    @Bean
    public PoseKeyPointExtractor poseKeypointExtractor(WebClient poseWebClient) {
        return new HttpPoseKeyPointExtractor(poseWebClient);
    }
}
