package com.seniorway.seniorway.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthUser {
    private Long id;
    private String role;
}
