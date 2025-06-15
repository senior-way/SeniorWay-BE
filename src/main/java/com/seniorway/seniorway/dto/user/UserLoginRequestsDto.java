package com.seniorway.seniorway.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestsDto {
    private String username;
    private String password;
    private String email;
}
