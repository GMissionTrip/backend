package com.gangchu.gangchutrip.route.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.route.dto.KakaoRouteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteService {
    private static final String KAKAO_ROUTE_URL = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";

    @Value("${kakao.route-api-key}")
    private String routeApiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Map<String, Object> getDirections(KakaoRouteRequestDto kakaoRouteRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK " + routeApiKey);

        HttpEntity<KakaoRouteRequestDto> entity = new HttpEntity<>(kakaoRouteRequestDto, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_ROUTE_URL, HttpMethod.POST, entity, String.class);
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("카카오 길찾기 API 호출 실패", e);
        }
    }
}