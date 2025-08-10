package com.seniorway.seniorway.service.oauth;

import com.seniorway.seniorway.dto.auth.TokenResponseDTO;
import com.seniorway.seniorway.dto.oauth.KakaoTokenResponse;
import com.seniorway.seniorway.dto.oauth.KakaoUserInfoResponse;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // application.yml에 설정된 값들을 주입받음
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;

    public TokenResponseDTO login(String code) {
        // 1. 카카오에 Access Token 요청
        KakaoTokenResponse kakaoTokenResponse = getAccessToken(code).block();

        // 2. Access Token으로 사용자 정보 요청
        KakaoUserInfoResponse kakaoUserInfoResponse = getUserInfo(kakaoTokenResponse.getAccessToken()).block();

        // 3. 사용자 정보를 바탕으로 DB에 사용자 저장 또는 update
        User user = saveOrUpdateUser(kakaoUserInfoResponse);

        // 4. 서비스 자체의 Access Token과 Refresh Token 생성
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 5. 두 토큰을 DTO에 담아 반환
        return new TokenResponseDTO(accessToken, refreshToken);
    }

    private Mono<KakaoTokenResponse> getAccessToken(String  code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        System.out.println("======= 카카오 토큰 요청 파라미터 =======");
        System.out.println("client_id: " + clientId);
        System.out.println("client_secret: " + clientSecret);
        System.out.println("redirect_uri: " + redirectUri);
        System.out.println("code: " + code);
        System.out.println("======================================");

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            System.err.println("카카오 토큰 요청 에러 응답: " + errorBody);
                            return Mono.error(new RuntimeException("카카오 토큰 요청 실패: " + errorBody));
                        }))
                .bodyToMono(KakaoTokenResponse.class);
    }
    private Mono<KakaoUserInfoResponse> getUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        return webClient.get()
                .uri(userInfoUri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class);
    }

    private User saveOrUpdateUser(KakaoUserInfoResponse kakaoUserInfoResponse) {
        User user = userRepository.findByKakaoId(kakaoUserInfoResponse.getId())
                .map(entity -> entity.update(kakaoUserInfoResponse.getProperties().getNickname(), kakaoUserInfoResponse.getKakaoAccount().getProfile().getProfileImageUrl()))
                .orElseGet(kakaoUserInfoResponse::toEntity);

        return userRepository.save(user);
    }
}
