package com.seniorway.seniorway.repository.review;

import com.seniorway.seniorway.entity.review.ReviewImageEntity;
import com.seniorway.seniorway.entity.review.PlaceReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {
    List<ReviewImageEntity> findByReview(PlaceReviewEntity review);
}
