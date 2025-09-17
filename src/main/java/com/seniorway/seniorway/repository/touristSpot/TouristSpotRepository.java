package com.seniorway.seniorway.repository.touristSpot;

import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TouristSpotRepository extends JpaRepository<TouristSpotEntity, Long> {
    // tourist_spot_id로 조회
    Optional<TouristSpotEntity> findByTouristSpotId(Long touristSpotId);

    // content_id로 조회
    TouristSpotEntity findByContentId(String contentId);
}
