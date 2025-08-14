package com.seniorway.seniorway.security;

import com.seniorway.seniorway.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * HTTP Authorization 헤더에서 JWT 토큰을 검증하고
     * 유효한 경우 속성을 채우기 위해 WebSocket 핸드셰이크 프로세스를 가로챕니다.
     *
     * @param request 핸드셰이크 중의 HTTP 요청
     * @param response 핸드셰이크 중의 HTTP 응답
     * @param wsHandler 핸드셰이크를 처리할 WebSocket 핸들러
     * @param attributes WebSocket 세션에 전달될 속성 맵
     * @return 핸드셰이크를 진행해야 하는 경우 true, 그렇지 않은 경우 false
     * @throws Exception 토큰 검증 또는 처리 중 오류가 발생하는 경우
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String token = null;

        // Http Header
        if (request instanceof ServletServerHttpRequest servletRequest) {
            token = servletRequest.getServletRequest().getHeader("Authorization");
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                attributes.put("user", auth.getPrincipal());
                return true;
            }
        }
        return false;
    }

    /**
     * 핸드셰이크가 완료된 후 호출됩니다. WebSocket 연결이 수립된 후
     * 로깅, 정리 또는 사용자 정의 처리와 같은 핸드셰이크 후 처리를 위한 기회를 제공합니다.
     *
     * @param request 핸드셰이크와 관련된 HTTP 요청
     * @param response 핸드셰이크와 관련된 HTTP 응답
     * @param wsHandler WebSocket 세션을 처리하는 WebSocket 핸들러
     * @param exception 핸드셰이크 중 발생한 예외, 예외가 발생하지 않은 경우 null
     */
    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
