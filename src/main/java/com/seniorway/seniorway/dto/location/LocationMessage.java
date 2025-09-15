package com.seniorway.seniorway.dto.location;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationMessage {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
