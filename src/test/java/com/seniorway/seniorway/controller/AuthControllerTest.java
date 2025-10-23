package com.seniorway.seniorway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seniorway.seniorway.dto.auth.UserLoginRequestsDTO;
import com.seniorway.seniorway.dto.auth.UserSignUpRequestsDTO;
import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String baseUrl;

    @BeforeEach
    void setUp() {
        int port = 8080;
        baseUrl = "http://localhost:" + port + "/auth";
        userRepository.deleteAll();
    }

    @Test
    void signupSuccess() {
        // given
        UserSignUpRequestsDTO signUpDto = new UserSignUpRequestsDTO();
        signUpDto.setUsername("testuser");
        signUpDto.setPassword("password123");
        signUpDto.setEmail("test@example.com");

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/signup",
                signUpDto,
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        userRepository.findByEmail("test@example.com");
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();

        User user = userRepository.findByEmail("test@example.com").get();
        assertThat(user.getRole()).isEqualTo("USER");
    }

    @Test
    void loginSuccess() {
        // given: 미리 사용자 등록
        User user = User.builder()
                .username("loginuser")
                .email("login@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        UserLoginRequestsDTO loginDto = new UserLoginRequestsDTO();
        loginDto.setEmail("login@example.com");
        loginDto.setPassword("password123");

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginDto,
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("accessToken");
        assertThat(response.getHeaders().get("Set-Cookie")).isNotEmpty();
    }
}
