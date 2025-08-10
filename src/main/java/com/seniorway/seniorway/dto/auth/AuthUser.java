package com.seniorway.seniorway.dto.auth;

import com.seniorway.seniorway.entity.user.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {
    private Long userId;
    private String email;
    private Role role;
}
