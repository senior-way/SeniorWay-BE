package com.seniorway.seniorway.repository.review;

import com.seniorway.seniorway.entity.review.ReviewCommentEntity;
import com.seniorway.seniorway.entity.review.PlaceReviewEntity;
import com.seniorway.seniorway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Long> {
    List<ReviewCommentEntity> findByReview(PlaceReviewEntity review);
    List<ReviewCommentEntity> findByUser(User user);
}
