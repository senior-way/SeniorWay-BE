package com.seniorway.seniorway.repository.touristAudioGuide;

import com.seniorway.seniorway.entity.touristAudioGuide.TouristAudioGuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouristAudioGuideRepository extends JpaRepository<TouristAudioGuideEntity, Long> {
    // tourist_spot_id로 오디오 가이드 리스트 조회
    List<TouristAudioGuideEntity> findByTouristSpot_TouristSpotId(Long touristSpotId);
}
