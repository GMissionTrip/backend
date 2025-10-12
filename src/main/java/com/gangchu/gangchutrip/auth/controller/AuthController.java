package com.gangchu.gangchutrip.auth.controller;


import com.gangchu.gangchutrip.auth.service.AuthService;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Tag(name ="인증", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;


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
        return authService.getAuthUrl(scope);
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
        return authService.handleAuthorizationCallback(code);
    }


    @PostMapping("/logout")
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
       return authService.logout();
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
        return authService.unlink();
    }

    // 아이디/비밀번호 로그인
    @PostMapping("/login")
    @Operation(
            summary = "아이디/비밀번호 로그인",
            description = "사용자 아이디와 비밀번호로 로그인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)))
            }
    )
    ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            if (username == null || password == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "아이디와 비밀번호를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            return authService.loginWithPassword(username, password);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    @Operation(
            summary = "비밀번호 변경",
            description = "사용자의 비밀번호를 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)))
            }
    )
    ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordRequest) {
        try {
            String username = passwordRequest.get("username");
            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");
            
            if (username == null || currentPassword == null || newPassword == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "모든 필드를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            return authService.changePassword(username, currentPassword, newPassword);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 회원가입
    @PostMapping("/register")
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)))
            }
    )
    ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");
            String nickname = registerRequest.get("nickname");
            String email = registerRequest.get("email");
            
            if (username == null || password == null || nickname == null || email == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "모든 필드를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            return authService.register(username, password, nickname, email);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
