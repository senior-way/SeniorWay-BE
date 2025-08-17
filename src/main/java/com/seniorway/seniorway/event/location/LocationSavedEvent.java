package com.seniorway.seniorway.event.location;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationSavedEvent {
    private final LocationMessage locationMessage;
}
