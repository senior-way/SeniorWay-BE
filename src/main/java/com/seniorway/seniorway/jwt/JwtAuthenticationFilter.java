package com.seniorway.seniorway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/auth/kakao",
            "/api/auth/login"
    );

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter called!");
        String token = jwtTokenProvider.resolveToken(request);  // 1. 요청 헤더에서 토큰 추출
        System.out.println("token: " + token);
        System.out.println("jwt: " + jwtTokenProvider.validateToken(token));
        if (token != null && jwtTokenProvider.validateToken(token)) {  // 2. 토큰 유효성 검사
            System.out.println("Token is valid, extracting authentication...");
            var authentication = jwtTokenProvider.getAuthentication(token);  // 3. 인증 정보 생성
            SecurityContextHolder.getContext().setAuthentication(authentication);  // 4. Spring Security에 등록
        } else {
            System.out.println("Token is invalid or null");
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
    }
}

