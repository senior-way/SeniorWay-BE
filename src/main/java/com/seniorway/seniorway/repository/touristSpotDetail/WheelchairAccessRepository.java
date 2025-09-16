package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.WheelchairAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WheelchairAccessRepository extends JpaRepository<WheelchairAccessEntity, Long> {
    boolean existsByContentId(String contentId);

    WheelchairAccessEntity findByContentId(String contentId);
}
