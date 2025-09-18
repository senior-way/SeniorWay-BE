package com.seniorway.seniorway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 로거 선언: 필터 내부 로그 출력을 위한 SLF4J Logger
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // 필터에서 토큰 검사 없이 통과시킬 URL 리스트 (예: 로그인, 카카오 인증 경로)
    private static final List<String> EXCLUDE_URLS = List.of(
            // 인증 관련
            "/api/auth/kakao",
            "/api/auth/login",
            "/api/auth/signup",
            // Swagger UI
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/api-docs",
            "/swagger-resources",
            "/webjars",
            "/favicon.ico",
            "/.well-known/appspecific",
            "/api/auth/signup",
            "/api/auth/guardian-signup",
            "/api/oauth/kakao/callback"

    );

    // JWT 토큰 생성, 검증, 인증정보 추출을 담당하는 프로바이더
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자: JwtTokenProvider를 주입받아 필터 인스턴스 생성
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 실제 요청마다 호출되는 필터 로직
     * - 요청 헤더에서 JWT 토큰을 추출하고
     * - 토큰 유효성 검사 후
     * - 인증 정보를 SecurityContext에 저장하여 인증 처리
     * - 토큰이 없거나 유효하지 않은 경우, 인증 없이 다음 필터로 넘김
     * - 예외 발생 시 로그를 남기고 401 응답 처리
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param chain    필터 체인 객체
     * @throws ServletException 서블릿 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 1. HTTP 요청 헤더에서 토큰을 추출한다.
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰이 존재하면 유효성 검사 진행
            if (token != null) {
                boolean valid = jwtTokenProvider.validateToken(token);
                logger.debug("jwt valid: {}", valid);

                // 3. 토큰이 유효하면 인증 정보를 가져와 SecurityContext에 저장
                if (valid) {
                    var authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // 토큰 처리 중 예외가 발생하면 로그 출력
            logger.error("Could not set user authentication in security context", ex);

            // SecurityContext 클리어 (인증정보 제거)
            SecurityContextHolder.clearContext();

            // 401 Unauthorized 응답 전송
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;  // 필터 체인 진행 중단
        }

        // 다음 필터로 요청/응답 전달
        chain.doFilter(request, response);
    }

    /**
     * 필터를 적용하지 않을 경로를 지정
     * - 로그인, 카카오 인증 등 토큰 검증 없이 접근 가능한 경로는 필터를 건너뜀
     *
     * @param request HTTP 요청 객체
     * @return true면 필터 미적용(건너뜀), false면 필터 적용
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 미적용 URL 리스트 중 하나라도 요청 경로로 시작하면 필터를 건너뜀
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
    }
}
