package com.gangchu.gangchutrip.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.auth.dto.KakaoTokenResponseDto;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
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
    private final JwtTokenProvider jwtUtil;

    // 메모리에 토큰 저장 (세션 대신)
    private String currentAccessToken = null;
    private Integer currentExpiresIn = null;

    private void saveAccessToken(String accessToken, Integer expiresIn) {
        this.currentAccessToken = accessToken;
        this.currentExpiresIn = expiresIn;
    }

    private String getAccessToken() {
        return this.currentAccessToken;
    }

    private void invalidateSession() {
        this.currentAccessToken = null;
        this.currentExpiresIn = null;
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
        try {
            // 테스트용으로 실제 카카오 API 호출을 우회하고 더미 데이터 사용
            System.out.println("카카오 인증 코드 받음: " + code);
            
            // 더미 사용자 정보 생성
            String email = "test@kakao.com";
            String nickname = "카카오 사용자";
            String profileImageUrl = "https://via.placeholder.com/100x100/FF6B6B/FFFFFF?text=Kakao";
            
            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            // 회원 정보 저장 또는 업데이트
            if (memberRepository.existsByEmail(email)) {
                System.out.println("기존 사용자 로그인");
            } else {
                System.out.println("새 사용자 등록");
                Member member = new Member();
                member.setEmail(email);
                member.setNickname(nickname);
                member.setProfile_image_url(profileImageUrl);
                member.setCreated_at(Date.valueOf(java.time.LocalDate.now()));
                member.setPoint(0);
                memberRepository.save(member);
            }
            
            // 프론트엔드로 토큰 전달하는 HTML 생성
            String html = """
            <script>
                window.opener.postMessage({accessToken: '%s', refreshToken: '%s'}, 'http://localhost:3000');
                window.close();
            </script>"""
                    .formatted(accessToken, refreshToken);
            System.out.println("HTML Response: " + html);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
            
        } catch (Exception e) {
            System.out.println("Error in handleAuthorizationCallback: " + e.getMessage());
            e.printStackTrace();
            String html = """
                <script>
                window.opener.postMessage({error: '%s'}, 'http://localhost:3000');
                window.close();
                </script>""".formatted(e.getMessage());
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_HTML).body(html);
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

    // 아이디/비밀번호 로그인
    public ResponseEntity<?> loginWithPassword(String username, String password) {
        try {
            // 테스트용 더미 사용자 데이터
            Map<String, String> dummyUsers = new LinkedHashMap<>();
            dummyUsers.put("test1", "gangchutest1234@");
            dummyUsers.put("admin", "admin123");
            dummyUsers.put("user", "password123");
            
            // 비밀번호 검증
            if (!dummyUsers.containsKey(username) || !dummyUsers.get(username).equals(password)) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);
            
            // 사용자 정보 생성
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("id", username);
            userData.put("username", username);
            userData.put("nickname", username.equals("test1") ? "테스트 사용자" : username);
            userData.put("email", username + "@example.com");
            userData.put("profileImage", "https://via.placeholder.com/100x100/4CAF50/FFFFFF?text=" + String.valueOf(username.charAt(0)).toUpperCase());
            userData.put("level", 1);
            userData.put("gender", "unknown");
            
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("data", Map.of(
                "user", userData,
                "accessToken", accessToken,
                "refreshToken", refreshToken
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 비밀번호 변경
    public ResponseEntity<?> changePassword(String username, String currentPassword, String newPassword) {
        try {
            // 테스트용 더미 사용자 데이터
            Map<String, String> dummyUsers = new LinkedHashMap<>();
            dummyUsers.put("test1", "gangchutest1234@");
            dummyUsers.put("admin", "admin123");
            dummyUsers.put("user", "password123");
            
            // 현재 비밀번호 검증
            if (!dummyUsers.containsKey(username) || !dummyUsers.get(username).equals(currentPassword)) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "현재 비밀번호가 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 새 비밀번호로 업데이트 (실제로는 DB에 저장해야 함)
            dummyUsers.put(username, newPassword);
            
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 회원가입
    public ResponseEntity<?> register(String username, String password, String nickname, String email) {
        try {
            // 테스트용 더미 사용자 데이터
            Map<String, String> dummyUsers = new LinkedHashMap<>();
            dummyUsers.put("test1", "gangchutest1234@");
            dummyUsers.put("admin", "admin123");
            dummyUsers.put("user", "password123");
            
            // 중복 아이디 체크
            if (dummyUsers.containsKey(username)) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "이미 존재하는 아이디입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 새 사용자 추가 (실제로는 DB에 저장해야 함)
            dummyUsers.put(username, password);
            
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 성공적으로 완료되었습니다.");
            response.put("data", Map.of(
                "username", username,
                "nickname", nickname,
                "email", email
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}