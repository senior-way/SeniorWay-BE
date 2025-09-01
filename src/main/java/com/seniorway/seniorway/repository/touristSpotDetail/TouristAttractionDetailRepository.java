package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.TouristAttractionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TouristAttractionDetailRepository extends JpaRepository<TouristAttractionDetailEntity, Integer> {
    // ...custom query methods if needed...
}