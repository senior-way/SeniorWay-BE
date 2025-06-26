package com.seniorway.seniorway.controller.auth;

import com.seniorway.seniorway.dto.auth.KakaoLoginRequest;
import com.seniorway.seniorway.dto.auth.TokenResponse;
import com.seniorway.seniorway.service.auth.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoOAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    @PostMapping("kakao-login")
    public ResponseEntity<TokenResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request) {
        TokenResponse tokenResponse = kakaoOAuthService.loginWithKakao(request.getAccessToken());
        return ResponseEntity.ok(tokenResponse);
    }
}
