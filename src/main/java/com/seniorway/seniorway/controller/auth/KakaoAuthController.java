package com.seniorway.seniorway.controller.auth;

import com.seniorway.seniorway.dto.auth.TokenResponseDTO;
import com.seniorway.seniorway.dto.oauth.KakaoAuthRequest;
import com.seniorway.seniorway.service.oauth.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/kakao/login2")
    public ResponseEntity<Map<String, String>> handleKakaoCallback(
            @RequestBody KakaoAuthRequest kakaoAuthRequest
    ) {
        TokenResponseDTO tokenResponseDTO = kakaoLoginService.login(kakaoAuthRequest.getCode());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponseDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        // 3. Access Token을 꺼내서 JSON Body 생성
        Map<String, String> responseBody = Map.of("accessToken", tokenResponseDTO.getAccessToken());

        // 4. 쿠키는 헤더에, Access Token은 바디에 담아 최종 응답 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);
    }
}
