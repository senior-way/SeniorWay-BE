package com.seniorway.seniorway.repository.review;

import com.seniorway.seniorway.entity.review.PlaceReviewEntity;
import com.seniorway.seniorway.entity.place.PlaceEntity;
import com.seniorway.seniorway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReviewEntity, Long> {
    List<PlaceReviewEntity> findByPlace(PlaceEntity place);
    List<PlaceReviewEntity> findByUser(User user);
}
