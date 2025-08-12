package com.seniorway.seniorway.dto.auth;

import com.seniorway.seniorway.enums.user.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDTO {
    private Long userId;
    private String email;
    private Role role;
}
