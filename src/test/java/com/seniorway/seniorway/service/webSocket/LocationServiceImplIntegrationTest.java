package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {LocationServiceImpl.class})
@ActiveProfiles("test")
class LocationServiceImplIntegrationTest {

    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void handleLocation_redis_store_test() {
        // given
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.setUserId(1L);
        locationMessage.setLatitude(37.5665);
        locationMessage.setLongitude(126.9780);

        CustomUserDetails userDetails =
                new CustomUserDetails(1L, "user@test.com", "password", "USER", null);

        // when
        locationService.handleLocation(locationMessage, userDetails);

        // then - Redis 저장 검증
        String key = "location: " + locationMessage.getUserId();
        String latest = redisTemplate.opsForValue().get(key);

        assertThat(latest).isNotNull();
        assertThat(latest).contains("37.5665");

        // history 리스트도 검증
        Long size = redisTemplate.opsForList().size("location:history:" + locationMessage.getUserId());
        assertThat(size).isGreaterThan(0);
    }
}
