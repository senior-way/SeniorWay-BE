package com.seniorway.seniorway.utils.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ProximityThrottle {
    private final StringRedisTemplate redis;

    public boolean acquireSpotOnce(Long userId, Long spotId, Duration ttl) {
        String key = "mail:proximity:spot:" + userId + ":" + spotId;
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(ok);
    }
}
