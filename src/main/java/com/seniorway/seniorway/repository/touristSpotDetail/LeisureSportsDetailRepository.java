package com.seniorway.seniorway.repository.touristSpotDetail;

import com.seniorway.seniorway.entity.touristSpotDetail.LeisureSportsDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeisureSportsDetailRepository extends JpaRepository<LeisureSportsDetailEntity, Integer> {
    // ...custom query methods if needed...
}
