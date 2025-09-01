package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.FoodDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodDetailRepository extends JpaRepository<FoodDetailEntity, Integer> {
    // ...custom query methods if needed...
}
