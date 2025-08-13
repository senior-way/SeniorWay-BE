package com.seniorway.seniorway.repository.trip;

import com.seniorway.seniorway.entity.trip.TripPlanEntity;
import com.seniorway.seniorway.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripPlanRepository extends JpaRepository<TripPlanEntity, Long> {
    List<TripPlanEntity> findByUser(User user);
}
