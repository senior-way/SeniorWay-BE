package com.seniorway.seniorway.controller;

import com.seniorway.seniorway.config.JwtTokenProvider;
import com.seniorway.seniorway.dto.UserLoginRequestsDto;
import com.seniorway.seniorway.dto.UserSignUpRequestsDto;
import com.seniorway.seniorway.repository.UserRepository;
import com.seniorway.seniorway.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequestsDto userLoginRequest) {
        String refreshToken = "";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserSignUpRequestsDto userSignUpRequest) {
        String token = userService.signUp(userSignUpRequest);
        return ResponseEntity.ok("Signup successful");
    }
}
