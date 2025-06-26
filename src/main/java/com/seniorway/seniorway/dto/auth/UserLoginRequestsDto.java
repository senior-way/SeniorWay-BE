package com.seniorway.seniorway.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestsDto {
    private String username;
    private String password;
    private String email;
}
