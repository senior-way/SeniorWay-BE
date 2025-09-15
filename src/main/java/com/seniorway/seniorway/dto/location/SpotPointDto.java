package com.seniorway.seniorway.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@NoArgsConstructor
public class SpotPointDto {
    private Long touristSpotId;
    private String mapX;
    private String mapY;
    private Integer sequenceOrder;
}
