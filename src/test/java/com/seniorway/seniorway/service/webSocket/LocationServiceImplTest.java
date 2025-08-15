package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class LocationServiceImplTest {

    LocationServiceImpl locationService;
    SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        locationService = new LocationServiceImpl(messagingTemplate);
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

        // when: Service 호출
        locationService.handleLocation(locationMessage, userDetails);

        // then: 이 메시지가 올바른 destination 으로 전송되었는지 검증
        verify(messagingTemplate).convertAndSend("/topic/location/1", locationMessage);
    }
}