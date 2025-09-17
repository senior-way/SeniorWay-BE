package com.seniorway.seniorway.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserInfoResponseDto {

    private String username;
    private String email;
    private LocalDate birth;

}
