package com.seniorway.seniorway.controller;

import com.seniorway.seniorway.config.JwtTokenProvider;
import com.seniorway.seniorway.dto.user.UserLoginRequestsDto;
import com.seniorway.seniorway.dto.UserLoginResponseDTO;
import com.seniorway.seniorway.dto.UserSignUpRequestsDto;
import com.seniorway.seniorway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequestsDto userLoginRequest) {
        UserLoginResponseDTO userLoginResponseDTO = userService.login(userLoginRequest);
        String refreshToken = jwtTokenProvider.createRefreshToken(userLoginResponseDTO.getUserId());

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        Map<String, String> responseBody = Map.of("accessToken", refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody.toString());
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserSignUpRequestsDto userSignUpRequest) {
        userService.signUp(userSignUpRequest);
        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String role = "USER";
            String newAccessToken = jwtTokenProvider.createToken(userId, email, role);
            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logout successful");
    }

    @GetMapping("check-token")
    public ResponseEntity<?> checkToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired");
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email.toLowerCase());
        return ResponseEntity.ok(exists);
    }
}
