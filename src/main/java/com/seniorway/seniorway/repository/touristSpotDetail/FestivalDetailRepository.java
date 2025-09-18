package com.seniorway.seniorway.repository.touristSpotDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import com.seniorway.seniorway.entity.touristSpotDetail.FestivalDetailEntity;

public interface FestivalDetailRepository extends JpaRepository<FestivalDetailEntity, Long> {
    boolean existsByContentId(String contentId);
    FestivalDetailEntity findByContentId(String contentId);
}
