package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void handleLocation(LocationMessage msg, CustomUserDetails userDetails) {
        saveLocation(msg);
        sendLocationToGuardian(msg);
    }

    private void saveLocation(LocationMessage msg) {
        // 1) 최신 위치 저장
        String key = "location: " + msg.getUserId();
        String value = msg.getLatitude() + "," + msg.getLongitude() + "," + System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, value);

        // 2) 최근 위치 히스토리 (최대 10개)
        String historyKey = "location:history:" + msg.getUserId();
        redisTemplate.opsForList().leftPush(historyKey, value);
        redisTemplate.opsForList().trim(historyKey, 0, 9);

        // TODO: DB에 위치 히스토리 저장
    }

    private void sendLocationToGuardian(LocationMessage msg) {
        String destination = "/topic/location/" + msg.getUserId();
        messagingTemplate.convertAndSend(destination, msg);
    }
}
