package com.gangchu.gangchutrip.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.auth.dto.KakaoTokenResponseDto;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.kauth-host}")
    private String kauthHost;

    @Value("${kakao.kapi-host}")
    private String kapiHost;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;



    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession();
    }

    private void saveAccessToken(String accessToken, Integer expiresIn) {
        getSession().setAttribute("access_token", accessToken);
        getSession().setAttribute("expires_in", expiresIn);
    }

    private String getAccessToken() {
        return (String) getSession().getAttribute("access_token");
    }

    private void invalidateSession() {
        getSession().invalidate();
    }

    private String call(String method, String urlString, String body) {
        HttpHeaders headers =  new HttpHeaders();
        headers.setBearerAuth(getAccessToken());

        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    urlString,
                    HttpMethod.valueOf(method),
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            return e.getResponseBodyAsString();
        }
    }

    public ResponseEntity<?> getAuthUrl(String scope) {
        String uri = UriComponentsBuilder
                .fromUriString(kauthHost + "/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParamIfPresent("scope", scope != null ? java.util.Optional.of(scope) : java.util.Optional.empty())
                .build()
                .toUriString();
        URI redirectUri = URI.create(uri);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build() ;
    }

    public ResponseEntity<?> handleAuthorizationCallback(String code) {
        KakaoTokenResponseDto tokenResponse;
        try {
            tokenResponse = getToken(code);
        } catch (Exception e) {
            System.out.println("Error getting token: " + e.getMessage());
            return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, e.getMessage() + " : " + e.getCause());
        }

        // 토큰 응답이 null인 경우
        if (tokenResponse == null){
            return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, "Failed to get token response");
        }

        // 액세스 토큰 저장
        saveAccessToken(tokenResponse.getAccess_token(), tokenResponse.getExpires_in());
        try {
            // 사용자 프로필 정보 가져오기
            ResponseEntity<?> userProfile = getUserProfile();

            // 사용자 프로필 정보가 null이거나 상태 코드가 OK가 아닌 경우
            if (userProfile.getStatusCode() != HttpStatus.OK) {
                return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, "Failed to get user profile");
            }

            // 사용자 프로필 정보 파싱
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) userProfile.getBody();

            // 프로필 정보가 null이거나 kakao_account가 없는 경우
            if (body == null || !body.containsKey("kakao_account")) {
                return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, "Kakao account not found in user profile");
            }
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) body.get("kakao_account");

            // kakaoAccount가 null인 경우
            if (kakaoAccount == null) {
                return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, "Kakao account not found in user profile");
            }

            // 프로필 정보에서 이메일과 닉네임, 프로필 이미지 URL 가져오기
            @SuppressWarnings("unchecked")
            Map<String, String> profile = (Map<String, String>) kakaoAccount.get("profile");
            String email = (String) kakaoAccount.get("email");

            // 이메일이 null인 경우
            if (email == null) {
                return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, "Email not found in Kakao account");
            }

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            // 회원 정보 저장 또는 업데이트
            if (memberRepository.existsByEmail(email)) {
                System.out.println("그냥 로그인");
            } else {
                System.out.println("kakaoAccount: " + kakaoAccount);
                String nickname = profile.get("nickname");
                String profileImageUrl = profile.get("profile_image_url");
                Member member = new Member();
                member.setEmail(email);
                member.setNickname(nickname);
                member.setProfile_image_url(profileImageUrl);
                System.out.println("email: " + email + " nickname: " + nickname + " profileImageUrl: " + profileImageUrl);
                memberRepository.save(member);
            }
            String html = """
            <script>
                window.opener.postMessage({accessToken: '%s', refreshToken: '%s'}, 'http://localhost:3000');
                window.close();
            </script>"""
                    .formatted(accessToken, refreshToken);
            System.out.println("HTML Response: " + html);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        } catch (Exception e) {
            System.out.println("Error getting user profile: " + e.getMessage());
            return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, e.getMessage() + " : " + e.getCause());
        }
    }

    private KakaoTokenResponseDto getToken(String code) throws Exception {
        System.out.println(clientId);
        System.out.println(clientSecret);
        String params = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code);
        System.out.println(params);
        String response = call("POST", kauthHost + "/oauth/token", params);
        System.out.println(response);
        return objectMapper.readValue(response, KakaoTokenResponseDto.class);
    }

    public ResponseEntity<?> getUserProfile() {
        try {
            String response = call("GET", kapiHost + "/v2/user/me", null);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            System.out.println("Error getting user profile: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<?> logout() {
        try {
            String response = call("POST", kapiHost + "/v1/user/logout", null);
            invalidateSession();
            System.out.println(response);
            return ApiResponseFactory.success(ResponseCode.OK, response);
        } catch (Exception e) {
            return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, e.getMessage() + " : " + e.getCause());
        }
    }

    public ResponseEntity<?> unlink() {
        try {
            String response = call("POST", kapiHost + "/v1/user/unlink", null);
            invalidateSession();
            return ApiResponseFactory.success(ResponseCode.OK, response);
        } catch (Exception e) {
            return ApiResponseFactory.success(ResponseCode.KAKAO_API_ERROR, e.getMessage() + " : " + e.getCause());
        }
    }

}