package com.seniorway.seniorway.dto.oauth;

import com.seniorway.seniorway.entity.user.Role;
import com.seniorway.seniorway.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse {
    private Long id;
    private Properties properties;
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String email;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String profileImageUrl;
        }
    }

    public User toEntity() {
        String email = "";
        if (this.kakaoAccount != null && this.kakaoAccount.getEmail() != null) {
            email = this.kakaoAccount.getEmail() ;
        }
        // nickname이나 picture도 null 체크 필요
        String nickname = (this.properties != null && this.properties.getNickname() != null)
                ? this.properties.getNickname() : "unknown";

        String picture = (this.kakaoAccount != null && this.kakaoAccount.getProfile() != null)
                ? this.kakaoAccount.getProfile().getProfileImageUrl() : "";

        System.out.println("email: " + email);
        System.out.println("nickname: " + nickname);
        System.out.println("picture: " + picture);

        return User.builder()
                .kakaoId(this.id)
                .email(email)
                .username(nickname)
                .picture(picture)
                .password("KAKAO_OAUTH_USER")  // TODO: 비밀번호 바꿔
                .role(Role.USER)
                .build();
    }
}