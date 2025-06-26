package com.seniorway.seniorway.entity.user;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(unique = true, nullable = false)
    private String username;  // 일반 로그인 시 사용, Kakao 로그인은 kakao nickname 사용

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = false)
    private String password;  // 일반 로그인은 암호화, Kakao는 "kakao" 같은 더미 문자열
    
    @Column
    private String role;
}
