package com.seniorway.seniorway.controller.profile;

import com.seniorway.seniorway.dto.auth.AuthUserDTO;
import com.seniorway.seniorway.dto.profile.UserProfileRequestDto;
import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.service.profile.UserProfileService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileEntity> createOrUpdateProfile(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDTO authUser, @RequestBody UserProfileRequestDto requestDto) {
        UserProfileEntity userProfile = userProfileService.createOrUpdateProfile(authUser.getUserId(), requestDto);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileEntity> getMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDTO authUser) {
        UserProfileEntity userProfile = userProfileService.getProfile(authUser.getUserId());
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping
    public ResponseEntity<List<UserProfileEntity>> getAll() {
        return ResponseEntity.ok(userProfileService.getAll());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDTO authUser) {
        userProfileService.deleteProfile(authUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
