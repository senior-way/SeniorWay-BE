package com.seniorway.seniorway.controller.profile;

import com.seniorway.seniorway.dto.profile.UserProfileRequestDto;
import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.security.CustomUserDetails;
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
    public ResponseEntity<UserProfileEntity> createOrUpdateProfile(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserProfileRequestDto requestDto) {
        UserProfileEntity userProfile = userProfileService.createOrUpdateProfile(userDetails.getUserId(), requestDto);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileEntity> getMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileEntity userProfile = userProfileService.getProfile(userDetails.getUserId());
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping
    public ResponseEntity<List<UserProfileEntity>> getAll() {
        return ResponseEntity.ok(userProfileService.getAll());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        userProfileService.deleteProfile(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}