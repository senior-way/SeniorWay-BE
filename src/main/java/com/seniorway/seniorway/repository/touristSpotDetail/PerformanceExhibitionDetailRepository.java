package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.PerformanceExhibitionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceExhibitionDetailRepository extends JpaRepository<PerformanceExhibitionDetailEntity, Integer> {
    boolean existsByContentId(String contentId);

    PerformanceExhibitionDetailEntity findByContentId(String contentId);
}
