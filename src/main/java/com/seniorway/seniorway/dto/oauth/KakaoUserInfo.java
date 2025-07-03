package com.seniorway.seniorway.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfo {
    private Long kakaoId;
    private String email;
    private String nickname;
}
