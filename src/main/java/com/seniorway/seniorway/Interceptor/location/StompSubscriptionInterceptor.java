package com.seniorway.seniorway.Interceptor.location;

import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.repository.location.GuardianRelationRepository;
import com.seniorway.seniorway.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompSubscriptionInterceptor implements ChannelInterceptor {

    private final GuardianRelationRepository guardianRelationRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Long wardId = Long.valueOf(destination.split("/")[3]);

            CustomUserDetails user = (CustomUserDetails) accessor.getUser();

            boolean allowed = guardianRelationRepository.existsByGuardianIdAndWardId(user.getUserId(), wardId);
            if (!allowed) {
                throw new CustomException(ErrorCode.GUARDIAN_ACCESS_DENIED);
            }
        }

        return message;
    }
}
