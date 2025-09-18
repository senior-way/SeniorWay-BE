package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.PetFriendlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetFriendlyInfoRepository extends JpaRepository<PetFriendlyEntity, Long> {
    boolean existsByContentId(String contentId);
    PetFriendlyEntity findByContentId(String contentId);
}
