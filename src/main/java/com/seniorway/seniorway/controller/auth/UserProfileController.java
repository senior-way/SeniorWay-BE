package com.seniorway.seniorway.controller.user;

import com.seniorway.seniorway.config.jwt.JwtTokenProvider;
import com.seniorway.seniorway.dto.user.UserProfileRequestDto;
import com.seniorway.seniorway.service.user.UserProfileService;
import com.seniorway.seniorway.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateProfile(@RequestBody @Valid UserProfileRequestDto dto,
                                                   HttpServletRequest request) {
        // 1. Authorization 헤더에서 토큰 추출 (예: "Bearer {token}")
        String token = jwtTokenProvider.resolveToken(request);

        // 2. 토큰 유효성 검사
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 5. 프로필 저장 서비스 호출
        userProfileService.saveOrUpdateUserProfile(userId, dto);
        return ResponseEntity.ok("Profile saved successfully");
    }
}
