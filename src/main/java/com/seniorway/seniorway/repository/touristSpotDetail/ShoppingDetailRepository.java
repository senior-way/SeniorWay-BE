package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.ShoppingDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingDetailRepository extends JpaRepository<ShoppingDetailEntity, Integer> {
    boolean existsByContentId(String contentId);

    ShoppingDetailEntity findByContentId(String contentId);
}
