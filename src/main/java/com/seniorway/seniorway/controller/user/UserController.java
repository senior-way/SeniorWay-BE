package com.seniorway.seniorway.controller.user;

import com.seniorway.seniorway.converter.user.UserConverter;
import com.seniorway.seniorway.dto.user.UserInfoResponseDto;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserInfoResponseDto> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userDetails.getUserId());

        UserInfoResponseDto responseDto = UserConverter.toDto(user);

        return ResponseEntity.ok(responseDto);
    }
}
