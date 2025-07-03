package com.seniorway.seniorway.controller.auth;

import com.seniorway.seniorway.dto.auth.TokenResponse;
import com.seniorway.seniorway.dto.oauth.KakaoLoginRequest;
import com.seniorway.seniorway.service.auth.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/**
 * 카카오 소셜 로그인을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 클라이언트에서 받은 카카오 Access Token을 사용하여 로그인/회원가입을 처리하고,
     * 서비스 자체의 Access Token과 Refresh Token을 발급합니다.
     * @param request 클라이언트로부터 받은 카카오 Access Token
     * @return Access Token은 Body에, Refresh Token은 HttpOnly 쿠키에 담아 반환
     */
    @PostMapping("/kakao/login")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        System.out.println("kakao login request: " + request);

        // 1. 카카오 Access Token으로 서비스 로직(로그인/회원가입) 처리 후, 자체 토큰 응답 받기
        TokenResponse tokenResponse = kakaoOAuthService.loginWithKakao(request.getKakaoAccessToken());

        // 2. Refresh Token을 HttpOnly 쿠키로 설정 (AuthController와 동일한 보안 패턴)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만 쿠키 전송
                .path("/")    // 쿠키의 유효 경로 설정
                .maxAge(Duration.ofDays(7)) // 쿠키 만료 시간 설정
                .build();

        // 3. Access Token은 JSON 바디로 반환
        Map<String, String> responseBody = Map.of("accessToken", tokenResponse.getAccessToken());

        // 4. 쿠키는 헤더에, Access Token은 바디에 담아 최종 응답 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);
    }
}