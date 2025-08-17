package com.seniorway.seniorway.listener.location;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import com.seniorway.seniorway.entity.location.UserLocation;
import com.seniorway.seniorway.event.location.LocationSavedEvent;
import com.seniorway.seniorway.repository.location.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LocationEventListener {

    private final UserLocationRepository userLocationRepository;

    @Async
    @EventListener
    public void handleLocationSaved(LocationSavedEvent event) {
        LocationMessage msg = event.getLocationMessage();

        UserLocation location = UserLocation.builder()
                .userId(msg.getUserId())
                .latitude(msg.getLatitude())
                .longitude(msg.getLongitude())
                .timestamp(LocalDateTime.now())
                .build();

        userLocationRepository.save(location);
    }
}
