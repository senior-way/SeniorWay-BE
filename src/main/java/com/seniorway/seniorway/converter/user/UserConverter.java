package com.seniorway.seniorway.converter.user;

import com.seniorway.seniorway.dto.user.UserInfoResponseDto;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.exception.CustomException;

public class UserConverter {

    public static UserInfoResponseDto toDto(User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return UserInfoResponseDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .birth(user.getBirth())
                .build();
    }
}
