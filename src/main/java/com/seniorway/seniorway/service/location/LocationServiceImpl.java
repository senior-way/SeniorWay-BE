package com.seniorway.seniorway.service.location;

import com.seniorway.seniorway.converter.location.LocationConverter;
import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.entity.location.UserLocation;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.event.location.LocationSavedEvent;
import com.seniorway.seniorway.event.location.LocationUpdatedEvent;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.repository.location.UserLocationRepository;
import com.seniorway.seniorway.repository.user.UserGuardianLinkRepository;
import com.seniorway.seniorway.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserLocationRepository userLocationRepository;
    private final UserGuardianLinkRepository userGuardianLinkRepository;

    @Override
    public void handleLocation(LocationMessage msg, CustomUserDetails userDetails) {
        saveLocation(msg);
        applicationEventPublisher.publishEvent(new LocationSavedEvent(msg));
        sendLocationToGuardian(msg);
    }

    @Transactional
    public void saveLocation(LocationMessage msg) {
        // 1) 최신 위치 저장 (Hash)
        String key = "location: " + msg.getUserId();
        redisTemplate.opsForHash().put(key, "latitude", String.valueOf(msg.getLatitude()));
        redisTemplate.opsForHash().put(key, "longitude", String.valueOf(msg.getLongitude()));
        redisTemplate.opsForHash().put(key, "timestamp", String.valueOf(msg.getTimestamp()));

        // 2) 최근 위치 히스토리 (최대 10개)
        String historyKey = "location:history:" + msg.getUserId();
        String value = msg.getLatitude() + "," + msg.getLongitude() + "," + System.currentTimeMillis();
        redisTemplate.opsForList().leftPush(historyKey, value);
        redisTemplate.opsForList().trim(historyKey, 0, 9);

        // 3) DB 저장
        UserLocation userLocation = UserLocation.builder()
                .userId(msg.getUserId())
                .latitude(msg.getLatitude())
                .longitude(msg.getLongitude())
                .timestamp(LocalDateTime.now())
                .build();

        userLocationRepository.save(userLocation);

        applicationEventPublisher.publishEvent(new LocationUpdatedEvent(msg.getUserId()));
    }

    private void sendLocationToGuardian(LocationMessage msg) {
        String destination = "/topic/location/" + msg.getUserId();
        messagingTemplate.convertAndSend(destination, msg);
    }

    @Override
    public LocationMessage getProtectedUserLastLocation(Long guardianId) {
        return userGuardianLinkRepository.findByGuardianId(guardianId)
                .stream()
                .findFirst().flatMap(link -> userLocationRepository.findTopByUserIdOrderByTimestampDesc(link.getUser().getId())
                        .map(LocationConverter::toDto))
                .orElse(null);
    }
}
