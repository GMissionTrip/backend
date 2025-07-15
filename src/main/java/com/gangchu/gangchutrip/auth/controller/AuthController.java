package com.gangchu.gangchutrip.auth.controller;


import com.gangchu.gangchutrip.auth.service.KakaoApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Tag(name ="인증", description = "인증 관련 API")
public class AuthController {

    private final KakaoApiService kakaoApiService;


    @GetMapping("/authorization")
    @Operation(
            summary = "카카오 인증 URL 생성 자동으로 /redirect로 리다이렉트",
            description = "카카오 인증을 위한 URL을 생성합니다. 선택적으로 scope를 지정할 수 있습니다.",
            parameters = {
                    @Parameter(name = "scope",
                            description = "요청할 권한의 범위 (예: 'profile, friends')")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 URL 생성 성공" ,
                                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Map.class)))})
    public ResponseEntity<?> authorization(@RequestParam(required = false) String scope) {
        return kakaoApiService.getAuthUrl(scope);
    }


    @GetMapping("/redirect")
    @Operation(
            summary = "카카오 인증 리다이렉트 처리",
            description = "카카오 인증 후 리다이렉트된 URL에서 인증 코드를 받아 처리합니다. " +
                    "인증 성공 시, access token과 refresh token을 클라이언트로 전달합니다.",
            parameters = {
                    @Parameter(name = "code", description = "카카오 인증 코드", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 성공, 토큰 전달",
                                    content = @Content(mediaType = MediaType.TEXT_HTML_VALUE, examples = @ExampleObject(
                                    name = "SuccessResponse",
                                            value = """
                                                    <script>
                                                        window.opener.postMessage({accessToken: 'your_access_token', refreshToken: 'your_refresh_token'}, 'http://localhost:3000');
                                                        window.close();
                                                    </script>"""))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "ErrorResponse",
                                            value = "{\"error\": \"Invalid request\"}")))})
    ResponseEntity<?> handleRedirect(@RequestParam String code) {
        return kakaoApiService.handleAuthorizationCallback(code);
    }


    @GetMapping("/logout")
    @Operation(
            summary = "카카오 로그아웃",
            description = "카카오 로그아웃을 수행합니다. 세션에서 access token을 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                            name = "ErrorResponse",
                                            value = "{\"error\": \"Invalid request\"}"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<?> logout() {
       return kakaoApiService.logout();
    }
    @GetMapping("/unlink")
    @Operation(
            summary = "카카오 계정 연결 해제",
            description = "카카오 계정과의 연결을 해제합니다. 세션에서 access token을 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "연결 해제 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "SuccessResponse",
                                            value = "{\"id\": \"123456789\"}")
                            )),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "ErrorResponse",
                                            value = "{\"error\": \"Invalid request\"}"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<?> unlink() {
        return kakaoApiService.unlink();
    }
}
