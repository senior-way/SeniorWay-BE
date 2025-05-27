package com.seniorway.seniorway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequestsDto {
    private String username;
    private String password;
    private String email;
    private String role;
}
