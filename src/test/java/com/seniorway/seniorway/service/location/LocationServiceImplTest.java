package com.seniorway.seniorway.service.location;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

// LocationServiceImpl 와 MockConfig 만을 스프링 컨텍스츠에 로딩하여 테스트 실행
@SpringBootTest(classes = {LocationServiceImpl.class, LocationServiceImplTest.MockConfig.class})
class LocationServiceImplTest {

    /**
     * MockConfig는 테스트에 사용되는 Mock Bean들을 제공하는 테스트 구성 클래스입니다.
     * SimpMessagingTemplate, StringRedisTemplate, ApplicationEventPublisher와
     * Redis 작업(ValueOperations, ListOperations)에 대한 Mock 인스턴스들을 포함합니다.
     * <p>
     * 이 클래스는 사전 정의된 Mock Bean들을 제공하여 제어된 환경에서
     * 애플리케이션 로직을 분리하고 테스트할 수 있도록 합니다.
     * <p>
     * 이 클래스에 정의된 컴포넌트들:
     * - SimpMessagingTemplate: 메시징 기능 테스트를 위한 Mock
     * - StringRedisTemplate: Redis 상호작용 테스트를 위한 Mock
     * - ApplicationEventPublisher: 이벤트 발행 테스트를 위한 Mock
     * - ValueOperations: Redis value-level 작업 테스트를 위한 Mock
     * - ListOperations: Redis list-level 작업 테스트를 위한 Mock
     */
    @TestConfiguration
    static class MockConfig {
        @Bean
        SimpMessagingTemplate messagingTemplate() {
            return mock(SimpMessagingTemplate.class);  // STOMP 메시지 전송 Mock
        }

        @Bean
        StringRedisTemplate redisTemplate() {
            return mock(StringRedisTemplate.class);  // Redis 템플릿 Mock
        }

        @Bean
        ApplicationEventPublisher applicationEventPublisher() {
            return mock(ApplicationEventPublisher.class);  // Event 발행 Mock
        }

        @Bean
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations() {
            return mock(ValueOperations.class);  // Redis String(Key-Value) 연산 Mock
        }

        @Bean
        @SuppressWarnings("unchecked")
        ListOperations<String, String> listOperations() {
            return mock(ListOperations.class);  // Redis List 연산 Mock
        }
    }

    @Autowired
    LocationServiceImpl locationService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ValueOperations<String, String> valueOperations;

    @Autowired
    ListOperations<String, String> listOperations;

    /**
     * 각 테스트 실행 전에 필요한 mock 동작과 설정을 세팅합니다.
     * 이 메서드는 테스트 클래스의 각 테스트 메서드 실행 전에 호출됩니다.
     * <p>
     * RedisTemplate 작업의 mock 동작을 설정합니다:
     * - redisTemplate의 `opsForValue()` 메서드가 mock valueOperations를 반환하도록 설정
     * - redisTemplate의 `opsForList()` 메서드가 mock listOperations를 반환하도록 설정
     * <p>
     * 이를 통해 실제 Redis 서버 연결 없이 격리된 환경에서
     * Redis 작업을 테스트할 수 있습니다.
     */
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void handleLocation_message_send_test() {

        // given
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.setUserId(1L);
        locationMessage.setLatitude(37.5665);
        locationMessage.setLongitude(126.9780);

        CustomUserDetails userDetails =
                new CustomUserDetails(1L, "user@test.com", "password", "USER", null);

        // when
        locationService.handleLocation(locationMessage, userDetails);

        // then
        // 메시지 전송 검증
        // 1. 메시지가 STOMP topic 으로 전송되었는지 확인
        verify(messagingTemplate).convertAndSend("/topic/location/1", locationMessage);

        // Redis 저장 검증
        String key = "location: " + locationMessage.getUserId();

        // ValueOperation.set() 호출 확인 (현재 위치 저장)
        verify(valueOperations).set(startsWith(key), anyString());

        // ListOperations.leftPush() 호출 확인 (히스토리 추가)
        verify(listOperations).leftPush(startsWith("location:history:" + locationMessage.getUserId()), anyString());
        
        // 히스토리 사이즈가 최대 10개로 유지되도록 trim 호출 확인
        verify(listOperations).trim("location:history:" + locationMessage.getUserId(), 0, 9);
    }
}