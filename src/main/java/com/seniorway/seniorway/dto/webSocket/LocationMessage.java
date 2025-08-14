package com.seniorway.seniorway.dto.webSocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationMessage {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private long timestamp;
}
