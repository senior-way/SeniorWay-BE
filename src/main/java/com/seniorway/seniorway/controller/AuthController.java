package com.seniorway.seniorway.controller;

import com.seniorway.seniorway.config.JwtTokenProvider;
import com.seniorway.seniorway.dto.UserLoginRequestsDto;
import com.seniorway.seniorway.dto.UserLoginResponseDTO;
import com.seniorway.seniorway.dto.UserSignUpRequestsDto;
import com.seniorway.seniorway.repository.UserRepository;
import com.seniorway.seniorway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
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
}
