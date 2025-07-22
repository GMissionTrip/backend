package com.gangchu.gangchutrip.route.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.route.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private static final String KAKAO_ROUTE_URL = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";

    @Value("${kakao.route-api-key}")
    private String routeApiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ResponseEntity<ApiResponse<KakaoRouteResponseDto>> getDirections(RouteRequestDto request) {
        try {
            ResponseEntity<ApiResponse<KakaoRouteResponseDto>> validationError = validateRequest(request);
            if (validationError != null) {
                return validationError;
            }

            KakaoRouteRequestDto kakaoRequest = convertToKakaoRequest(request, request.getWaypoints());
            KakaoRouteResponseDto response = callKakaoApi(kakaoRequest);
            return ApiResponseFactory.success(ResponseCode.ROUTE_FOUND, response);
        } catch (RestClientException e) {
            return ApiResponseFactory.error(ResponseCode.KAKAO_API_ERROR,
                    "카카오 길찾기 API 호출 실패: REST 요청 오류 - " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ApiResponseFactory.error(ResponseCode.KAKAO_API_ERROR,
                    "카카오 길찾기 API 호출 실패: JSON 처리 오류 - " + e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.error(ResponseCode.INTERNAL_SERVER_ERROR,
                    "카카오 길찾기 API 호출 중 알 수 없는 오류: " + e.getMessage());
        }
    }

    public ResponseEntity<ApiResponse<KakaoRouteResponseDto>> getOptimizedDirections(RouteRequestDto request, List<CoordinateDto> optimizedWaypoints) {
        try {
            ResponseEntity<ApiResponse<KakaoRouteResponseDto>> validationError = validateRequest(request);
            if (validationError != null) {
                return validationError;
            }

            KakaoRouteRequestDto kakaoRequest = convertToKakaoRequest(request, optimizedWaypoints);
            KakaoRouteResponseDto response = callKakaoApi(kakaoRequest);
            return ApiResponseFactory.success(ResponseCode.ROUTE_OPTIMIZED, response);
        } catch (RestClientException e) {
            return ApiResponseFactory.error(ResponseCode.KAKAO_API_ERROR,
                    "카카오 길찾기 API 호출 실패: REST 요청 오류 - " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ApiResponseFactory.error(ResponseCode.KAKAO_API_ERROR,
                    "카카오 길찾기 API 호출 실패: JSON 처리 오류 - " + e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.error(ResponseCode.INTERNAL_SERVER_ERROR,
                    "카카오 길찾기 API 호출 중 알 수 없는 오류: " + e.getMessage());
        }
    }

    private ResponseEntity<ApiResponse<KakaoRouteResponseDto>> validateRequest(RouteRequestDto request) {
        if (request.getOrigin() == null) {
            return ApiResponseFactory.error(ResponseCode.MISSING_ORIGIN, "origin은 필수입니다.");
        }
        if (request.getDestination() == null) {
            return ApiResponseFactory.error(ResponseCode.MISSING_DESTINATION, "destination은 필수입니다.");
        }
        return null;
    }

    private KakaoRouteResponseDto callKakaoApi(KakaoRouteRequestDto request) throws RestClientException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK " + routeApiKey);

        HttpEntity<KakaoRouteRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_ROUTE_URL, HttpMethod.POST, entity, String.class);
        return objectMapper.readValue(response.getBody(), KakaoRouteResponseDto.class);
    }

    private KakaoRouteRequestDto convertToKakaoRequest(RouteRequestDto request, List<CoordinateDto> waypoints) {
        return KakaoRouteRequestDto.builder()
                .origin(coordToMap(request.getOrigin()))
                .destination(coordToMap(request.getDestination()))
                .waypoints(waypointsToMapList(waypoints == null ? new ArrayList<>() : waypoints))
                .priority("RECOMMEND")
                .carFuel("GASOLINE")
                .carHipass(false)
                .alternatives(false)
                .roadDetails(true)
                .summary(true)
                .build();
    }

    private Map<String, Object> coordToMap(CoordinateDto c) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", c.getX());
        map.put("y", c.getY());
        if (c.getName() != null) {
            map.put("name", c.getName());
        }
        return map;
    }

    private List<Map<String, Object>> waypointsToMapList(List<CoordinateDto> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list != null) {
            for (CoordinateDto c : list) {
                result.add(coordToMap(c));
            }
        }
        return result;
    }
}