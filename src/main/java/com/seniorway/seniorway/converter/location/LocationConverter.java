package com.seniorway.seniorway.converter.location;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.entity.location.UserLocation;

public class LocationConverter {

    /** Entity → DTO */
    public static LocationMessage toDto(UserLocation entity) {
        if(entity == null) return null;

        return LocationMessage.builder()
                .userId(entity.getUserId())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .timestamp(entity.getTimestamp())
                .build();
    }

    /** DTO → Entity */
    public static UserLocation toEntity(LocationMessage dto) {
        if(dto == null) return null;

        return UserLocation.builder()
                .userId(dto.getUserId())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .timestamp(dto.getTimestamp())
                .build();
    }
}