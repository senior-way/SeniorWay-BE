package com.seniorway.seniorway.entity.user;


import com.seniorway.seniorway.enums.user.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private String username;  // 일반 로그인 시 사용, Kakao 로그인은 kakao nickname 사용

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = false)
    private String password;  // 일반 로그인은 암호화, Kakao는 "kakao" 같은 더미 문자열

    @Column(nullable = true)
    private LocalDate birth;

    // 프로필 사진 URL
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    public User update(String username, String picture) {
        this.username = username;
        this.picture = picture;
        return this;
    }

    // 나중에 카카오 연결
    public User updateKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    // Spring Security에서 사용할 권한 키를 반환하는 메소드
    public String getRoleKey() {
        return this.role.getKey();
    }
}
