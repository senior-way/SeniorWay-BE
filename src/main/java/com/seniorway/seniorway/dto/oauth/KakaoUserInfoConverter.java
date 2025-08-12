package com.seniorway.seniorway.dto.oauth;

import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.entity.user.User;

public class KakaoUserInfoConverter {

    public static User toEntity(KakaoUserInfoResponse dto) {
        String email = "";
        if (dto.getKakaoAccount() != null && dto.getKakaoAccount().getEmail() != null) {
            email = dto.getKakaoAccount().getEmail();
        }

        String nickname = (dto.getProperties() != null && dto.getProperties().getNickname() != null)
                ? dto.getProperties().getNickname() : "unknown";

        String picture = (dto.getKakaoAccount() != null && dto.getKakaoAccount().getProfile() != null)
                ? dto.getKakaoAccount().getProfile().getProfileImageUrl() : "";

        return User.builder()
                .kakaoId(dto.getId())
                .email(email)
                .username(nickname)
                .picture(picture)
                .password("KAKAO_OAUTH_USER")
                .role(Role.USER)
                .build();
    }
}
