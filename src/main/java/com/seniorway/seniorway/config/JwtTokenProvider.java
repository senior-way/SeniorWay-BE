package com.seniorway.seniorway.config;

import com.seniorway.seniorway.dto.AuthUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMilliseconds;  // 24시간

    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMilliseconds;  // 7일

    private SecretKey key;

    /**
     * JWT 서명을 위한 SecretKey 객체를 초기화하는 메서드
     * <p>
     *  {@code @PostConstruct} 가 붙어있어, Spring이 의존성을 주입한 후
     *  자동으로 호출
     * </p>
     * 
     * <p>
     *  {@code secretKey}를 Base64로 인코딩된 문자열을 디코딩하여
     *  바이트 배열로 변환한 뒤, JWT 서명에 적합한 {@link javax.crypto.SecretKey} 객체를 생성
     * </p>
     */
    @PostConstruct
    protected void init() {
        if (!secretKey.isBlank()) {
            // TODO 환경변수에서 Base64로 인코딩된 Key가 맞는지 확인할것
            byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    /**
     * 주어진 id와 email 과 role 를 이용하여 JWT Token을 생성
     * <p>
     *     Token의 claims 에는 userId를 subject로 설정하고, 추가로 email, role를 포함
     *     Token은 현재 시간부터 {@code expirationMilliseconds} mills 후 만료하도록 설정
     * </p>
     * @param userId userID
     * @param email user email
     * @param role user role
     * @return 생성된 JWT Token 문자열
     */
    public String createToken(Long userId, String email, String role) {
        // email 을 subject, role을 추가로 식별값으로 하여 Token 발급에 필요한 Claims 생성
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("email", email);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 주어진 email 을 이용하여 JWT Refresh Token 을 생성
     * <p>
     *     Token 의 claims에는 userId을 subject로만 설정
     *     일반 Token 보다 더 긴 시간을 유효기간으로 설정
     * </p>
     * @param userId userId
     * @return 생성된 JWT refresh Token 문자열
     */
    public String createRefreshToken(Long userId) {
        // email을 subject, type을 추가로 식별값으로 하여 Token 발급에 필요한 Claims 생성
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("type", "refreshToken");

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpirationMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Token 에서 userId(subject)를 추출
     * <p>
     *     Token의 서명을 검증하고, 유효한 경우, Token 에 포함된 subject 값을 반환
     *     Token이 만료되었건 변조되었으면 null을 반환
     * </p>
     * @param token JWT 문자열
     * @return Token에 포함돈 userId, 유효하지 않으면 null
     */
    public Long extractUserIdFromToken(String token) {
        try {
            return Long.parseLong(Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid or expired JWT token");
            return null;
        }
    }

    /**
     * 주어진 Jwt Token 에서 subject(userId) 를 추출
     * @param token
     * @return
     */
    public Long getUserIdFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        Claims claims = parser.parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT Token 에서 email claim을 추출
     * @param token
     * @return
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    /**
     * 주어진 JWT 토근의 유효성을 검증함
     * <p>
     *     이 메서드는 토근의 서명이 유효한지, 형식이 올바른지, 만료된지 않았는지 확인
     *     {@link JwtException}이나 {@link IllegalArgumentException}이 발생할 경우,
     *     유효하지 않은 토큰으로 간주하고 {@code false}를 반환합니다.
     * </p>
     *
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 인증 정보를 생성하여 반환합니다.
     * <p>
     * 토큰을 파싱하여 userId와 role 정보를 추출하고,
     * 이를 바탕으로 {@link AuthUser} 객체를 생성합니다.
     * 그리고 권한 목록(List<SimpleGrantedAuthority>)을 만들어 {@link UsernamePasswordAuthenticationToken}을 반환합니다.
     * </p>
     *
     * @param token JWT 토큰 문자열
     * @return 인증 정보를 담은 {@link Authentication} 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = extractUserIdFromToken(token);
        String role = claims.get("role", String.class);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
        }

        AuthUser authUser = new AuthUser(userId, role);
        return new UsernamePasswordAuthenticationToken(authUser, token, authorities);
    }

    /**
     * 요청에서 JWT 토근을 추출
     * <p>
     *     이 메서드는 다음의 순서로 토근을 확인
     * </p>
     * <ol>
     *     <li>Authorization 헤더에서 "Bearer " 접수사를 가진 토큰</li>
     *     <li>쿠키 중 이름이 "refreshToken"인 쿠키의 값</li>
     * </ol>
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        // 1. Authorization 헤더 체크
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2. 쿠키 체크
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
