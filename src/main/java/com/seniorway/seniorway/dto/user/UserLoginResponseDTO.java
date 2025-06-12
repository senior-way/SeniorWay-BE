package com.seniorway.seniorway.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserLoginResponseDTO {
    private String token;
    private Long userId;
}
