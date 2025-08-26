package com.seniorway.seniorway.repository.schedule;

import com.seniorway.seniorway.entity.schedule.ScheduleTouristSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleTouristSpotRepository extends JpaRepository<ScheduleTouristSpotEntity, Long> {
    // schedule_id로 조회
    List<ScheduleTouristSpotEntity> findBySchedule_ScheduleId(Long scheduleId);
}

