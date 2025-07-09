package com.gangchu.gangchutrip.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.auth.dto.AuthResponseDto;
import com.gangchu.gangchutrip.auth.dto.KakaoTokenResponse;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoApiService {

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
//    private final RestClient restClient;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

//    public KakaoApiService(ObjectMapper objectMapper, RestClient.Builder restClientBuilder, MemberRepository memberRepository, JwtUtil jwtUtil) {
//        this.objectMapper = objectMapper;
//        this.restClient = restClientBuilder.baseUrl(kapiHost).build();
//        this.memberRepository = memberRepository;
//        this.jwtUtil = jwtUtil;
//    }

    public String createDefaultMessage() {
        return "template_object={\"object_type\":\"text\",\"text\":\"Hello, world!\",\"link\":{\"web_url\":\"https://developers.kakao.com\",\"mobile_web_url\":\"https://developers.kakao.com\"}}";
    }

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

    private String call(String method, String urlString, String body) throws Exception {

//        RestClient.RequestBodySpec requestSpec = restClient.method(HttpMethod.valueOf(method))
//                .uri(urlString)
//                .headers(headers -> headers.setBearerAuth(getAccessToken()));
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

    public String getAuthUrl(String scope) {
        return UriComponentsBuilder
                .fromUriString(kauthHost + "/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParamIfPresent("scope", scope != null ? java.util.Optional.of(scope) : java.util.Optional.empty())
                .build()
                .toUriString();
    }

    public AuthResponseDto handleAuthorizationCallback(String code) {
        try {
            KakaoTokenResponse tokenResponse = getToken(code);

            if (tokenResponse != null) {
                saveAccessToken(tokenResponse.getAccess_token(), tokenResponse.getExpires_in());
                ResponseEntity<?> userProfile = getUserProfile();
                System.out.println(userProfile.getBody());
                LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) userProfile.getBody();
                Map<String, Object> kakaoAccount = (Map<String, Object>)body.get("kakao_account");
                Map<String, String> profile = (Map<String, String>)kakaoAccount.get("profile");
                String email = (String)kakaoAccount.get("email");
                System.out.println("email: " + email);
                String accessToken = jwtUtil.generateAccessToken(email);
                String refreshToken = jwtUtil.generateRefreshToken(email);
                AuthResponseDto data = new AuthResponseDto(accessToken, refreshToken);
                if(memberRepository.existsByEmail(email)){
                    System.out.println("그냥 로그인");
                }
                else{
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
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private KakaoTokenResponse getToken(String code) throws Exception {
        System.out.println(clientId);
        System.out.println(clientSecret);
        String params = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code);
        System.out.println(params);
        String response = call("POST", kauthHost + "/oauth/token", params);
        System.out.println(response);
        return objectMapper.readValue(response, KakaoTokenResponse.class);
    }

    public ResponseEntity<?> getUserProfile() {
        try {
            String response = call("GET", kapiHost + "/v2/user/me", null);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> getFriends() {
        try {
            String response = call("GET", kapiHost + "/v1/api/talk/friends", null);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> sendMessage(String messageRequest) {
        try {
            String response = call("POST", kapiHost + "/v2/api/talk/memo/default/send", messageRequest);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> sendMessageToFriend(String uuid, String messageRequest) {
        try {
            String response = call("POST",
                    kapiHost + "/v1/api/talk/friends/message/default/send?receiver_uuids=[" + uuid + "]",
                    messageRequest);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> logout() {
        try {
            String response = call("POST", kapiHost + "/v1/user/logout", null);
            invalidateSession();
            System.out.println(response);
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> unlink() {
        try {
            String response = call("POST", kapiHost + "/v1/user/unlink", null);
            invalidateSession();
            return ResponseEntity.ok(objectMapper.readValue(response, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}