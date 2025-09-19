package com.seniorway.seniorway.controller.user;

import com.seniorway.seniorway.dto.user.UserEmailDto;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.user.UserGuardianLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-guardians")
public class UserGuardianLinkController {

    private final UserGuardianLinkService userGuardianLinkService;

    @PostMapping("/link")
    public ResponseEntity<String> linkUserAndGuardian(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserEmailDto dto
    ) {
        userGuardianLinkService.linkGuardianIdToUserEmail(userDetails.getUserId(), dto.getUserEmail());

        return ResponseEntity.ok("User and guardian linked successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> hasWard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long guardianId = userDetails.getUserId();
        boolean hasWard = userGuardianLinkService.hasWard(guardianId);
        return ResponseEntity.ok(hasWard);
    }

    @GetMapping("/get-ward-email")
    public ResponseEntity<String> getWardEmail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long guardianId = userDetails.getUserId();
        String wardEmail = userGuardianLinkService.getWardEmail(guardianId);
        return ResponseEntity.ok(wardEmail);
    }
}
