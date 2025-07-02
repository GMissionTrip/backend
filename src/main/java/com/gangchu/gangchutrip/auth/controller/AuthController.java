package com.gangchu.gangchutrip.auth.controller;


import com.gangchu.gangchutrip.auth.dto.AuthResponseDto;
import com.gangchu.gangchutrip.auth.service.KakaoApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller("/api")
@AllArgsConstructor
public class AuthController {

    private final KakaoApiService kakaoApiService;

    @GetMapping("/auth")
    RedirectView auth(@RequestParam(required = false) String scope) {
        return new RedirectView(kakaoApiService.getAuthUrl(scope));
    }

    @GetMapping("/redirect")
    ResponseEntity<?> handleRedirect(@RequestParam String code) {
        AuthResponseDto data = kakaoApiService.handleAuthorizationCallback(code);
//        return new RedirectView("/index.html?login=" + (isSuccess ? "success" : "error")); //테스트 용
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/logout")
    ResponseEntity<?> logout() {
       return kakaoApiService.logout();
    }

    @GetMapping("/unlink")
    ResponseEntity<?> unlink() {
        return kakaoApiService.unlink();
    }
}
