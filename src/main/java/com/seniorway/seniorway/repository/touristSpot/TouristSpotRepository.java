package com.seniorway.seniorway.repository.touristSpot;

import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TouristSpotRepository extends JpaRepository<TouristSpotEntity, Long> {
    // tourist_spot_id로 조회
    Optional<TouristSpotEntity> findByTouristSpotId(Long touristSpotId);
}
