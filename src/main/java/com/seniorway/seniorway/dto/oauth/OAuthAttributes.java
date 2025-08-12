package com.seniorway.seniorway.dto.oauth;

import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    // OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환해야 한다.
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        // 다른 소셜 로그인(google, naver 등)도 추가 가능
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오 응답에서 'kakao_account' 필드는 Map 형태이다.
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        // 'kakao_account' 안에 있는 'profile' 필드도 Map 형태이다.
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티 생성
    public User toEntity() {
        return User.builder()
                .username(name)
                .email(email)
                .picture(picture)
                .role(Role.USER) // 기본 권한
                .build();
    }
}
