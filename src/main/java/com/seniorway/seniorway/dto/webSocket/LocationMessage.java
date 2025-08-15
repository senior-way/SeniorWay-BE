package com.seniorway.seniorway.dto.webSocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationMessage {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private long timestamp;
}
