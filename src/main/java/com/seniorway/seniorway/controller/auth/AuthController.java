package com.seniorway.seniorway.controller.auth;

import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.dto.auth.UserLoginRequestsDTO;
import com.seniorway.seniorway.dto.auth.UserSignUpRequestsDTO;
import com.seniorway.seniorway.service.auth.AuthService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 요청을 처리하고, 성공 시 액세스 토큰과 리프레시 토큰을 반환
     * 리프레시 토큰은 HttpOnly 쿠키로 설정되어 클라이언트에 전달
     *
     * @param loginRequest 사용자 로그인 요청 데이터 (이메일, 비밀번호)
     * @return 액세스 토큰과 리프레시 토큰이 포함된 응답
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid UserLoginRequestsDTO loginRequest) {
        Map<String, String> tokens = authService.login(loginRequest);

        // refreshToken 쿠키 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(tokens);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserSignUpRequestsDTO userSignUpRequest) {
        authService.signUp(userSignUpRequest);
        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            Role role = Role.USER;
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
        boolean exists = authService.existsByEmail(email.toLowerCase());
        return ResponseEntity.ok(exists);
    }
}
