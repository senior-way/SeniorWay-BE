package com.seniorway.seniorway.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seniorway.seniorway.dto.oauth.KakaoUserInfo;
import com.seniorway.seniorway.dto.auth.TokenResponseDTO;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO loginWithKakao(String KakaoAccessToken) {
        KakaoUserInfo kakaoUser = getKakoUserInfo(KakaoAccessToken);

        // DB 에서 체크 후 없으면 생성
        User user = userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .orElseGet(() -> registerNewUser(kakaoUser));

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    private KakaoUserInfo getKakoUserInfo(String kakaoAccessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.KAKAO_API_ERROR);
        }

        try {
            // JSON parsing
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            Long id = json.get("id").asLong();
            String email = json.path("kakao_account").path("email").asText(null);
            String nickname = json.path("properties").path("nickname").asText("unknown");

            return new KakaoUserInfo(id, email, nickname);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH_FAILURE);
        }
    }

    private User registerNewUser(KakaoUserInfo kakaoUser) {
        User newUser = User.builder()
                .kakaoId(kakaoUser.getKakaoId())
                .email(kakaoUser.getEmail())
                .username("kakao_" + kakaoUser.getNickname())
                .role(Role.USER)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();

        return userRepository.save(newUser);
    }
}
