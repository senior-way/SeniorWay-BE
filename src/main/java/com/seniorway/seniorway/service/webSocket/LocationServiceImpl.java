package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handleLocation(LocationMessage msg, CustomUserDetails userDetails) {
        saveLocation(msg);
        sendLocationToGuardian(msg);
    }

    private void saveLocation(LocationMessage msg) {
        // TODO: Redis에 최근 위치 저장
        // TODO: DB에 위치 히스토리 저장
    }

    private void sendLocationToGuardian(LocationMessage msg) {
        String destination = "/topic/location/" + msg.getUserId();
        messagingTemplate.convertAndSend(destination, msg);
    }
}
