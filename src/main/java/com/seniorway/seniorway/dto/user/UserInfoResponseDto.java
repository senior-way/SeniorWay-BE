package com.seniorway.seniorway.dto.user;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class UserInfoResponseDto {

    private String username;
    private String email;
    private LocalDate birth;

}
