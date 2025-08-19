package com.seniorway.seniorway.controller.webSocket;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.webSocket.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @MessageMapping("/location")  // client -> server
    public void receiveLocation(LocationMessage msg,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        msg.setUserId(userDetails.getUserId());
        locationService.handleLocation(msg, userDetails);
    }
}