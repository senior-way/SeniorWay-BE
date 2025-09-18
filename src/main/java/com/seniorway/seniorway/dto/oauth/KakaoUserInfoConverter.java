package com.seniorway.seniorway.dto.oauth;

import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.entity.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

public class KakaoUserInfoConverter {

    /**
     * KakaoUserInfoResponse → User 엔티티 변환
     * 임의 패스워드 생성 + 암호화
     * @param dto 카카오 사용자 정보
     * @param passwordEncoder 비밀번호 암호화기
     * @return User 엔티티
     */
    public static User toEntity(KakaoUserInfoResponse dto, PasswordEncoder passwordEncoder) {
        String email = "";
        if (dto.getKakaoAccount() != null && dto.getKakaoAccount().getEmail() != null) {
            email = dto.getKakaoAccount().getEmail();
        }

        if (email.isEmpty()) {
            email = "kakao_" + dto.getId() + "@user.login"; // 이메일이 없을 경우 임의 생성
        }

        String nickname = (dto.getProperties() != null && dto.getProperties().getNickname() != null)
                ? dto.getProperties().getNickname() : "unknown";

        String picture = (dto.getKakaoAccount() != null && dto.getKakaoAccount().getProfile() != null)
                ? dto.getKakaoAccount().getProfile().getProfileImageUrl() : "";

        // 임의 문자열 생성 후 암호화
        String encodedPassword = passwordEncoder.encode("KAKAO_OAUTH_USER_" + UUID.randomUUID());

        return User.builder()
                .kakaoId(dto.getId())
                .email(email)
                .username(nickname)
                .picture(picture)
                .password(encodedPassword)
                .birth(LocalDate.ofEpochDay(1999-12-31))  // 임의 생년월일
                .role(Role.USER)
                .build();
    }
}