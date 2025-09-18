package com.seniorway.seniorway.controller.auth;

import com.seniorway.seniorway.dto.auth.TokenResponseDTO;
import com.seniorway.seniorway.service.oauth.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${spring.application.deploy}")
    private boolean cookieSecure;

    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoCallback(@RequestParam("code") String code) {

        // 1. Authorization Code로 로그인 처리
        TokenResponseDTO tokenResponseDTO = kakaoLoginService.login(code);

        // 2. Refresh Token은 HttpOnly 쿠키로만 저장
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponseDTO.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None") // cross-site 환경일 때 필요
                .build();

        // 3. Access Token은 Body로 내려줌 (프론트에서 localStorage 등에 저장)
        Map<String, String> responseBody = Map.of(
                "accessToken", tokenResponseDTO.getAccessToken()
        );

        System.out.println(tokenResponseDTO.getAccessToken());

        // 4. 쿠키 + Body 함께 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);
    }
}
