package com.seniorway.seniorway.service;

import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.dto.auth.UserLoginRequestsDTO;
import com.seniorway.seniorway.dto.auth.UserLoginResponseDTO;
import com.seniorway.seniorway.dto.auth.UserSignUpRequestsDTO;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.repository.user.UserRepository;
import com.seniorway.seniorway.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class authServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void loginSuccess() throws Exception {
//        // given
//        User user = User.builder()
//                .email("test@email.com")
//                .password("encoded")
//                .role(Role.USER)
//                .id(1L)
//                .build();
//
//        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches("password1", "encoded")).thenReturn(true);
//        when(jwtTokenProvider.createToken(1L, "test@email.com", Role.USER)).thenReturn("accessToken");
//
//        UserLoginRequestsDTO request = new UserLoginRequestsDTO();
//        request.setUsername("user1");
//        request.setPassword("password1");
//        request.setEmail("test@email.com");
//
//        // when
//        UserLoginResponseDTO result = authService.login(request);
//
//        // then
//        assertEquals("accessToken", result.getToken());
//        assertEquals(1L, result.getUserId());
//    }

    @Test
    void signupSuccess() throws Exception {
        // given
        UserSignUpRequestsDTO dto = new UserSignUpRequestsDTO();
        dto.setUsername("user1");
        dto.setPassword("password1");
        dto.setEmail("test@email.com");
        dto.setRole("ADMIN"); // 무시되므로 실제로는 "ROLE_USER"로 저장될 것

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password1")).thenReturn("encodedPass");

        User savedUser = User.builder()
                .username("user1")
                .email("test@email.com")
                .password("encodedPass")
                .role(Role.USER)
                .id(2L)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.createToken(2L, "test@email.com", Role.USER)).thenReturn("signupToken");

        // when
        String token = authService.signUp(dto);

        // then
        assertEquals("signupToken", token);
    }
}
