package com.seniorway.seniorway.config.websocket;

import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.security.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // 구독용 prefix
        registry.setApplicationDestinationPrefixes("/app");  // client -> server prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")  // endpoint
                .setAllowedOrigins("*")  // CORS 허용
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider))
                .withSockJS();  // SockJS fallback 지원
    }
}
