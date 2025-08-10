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
}