package com.seniorway.seniorway.repository.schedule;

import com.seniorway.seniorway.dto.location.SpotPointDto;
import com.seniorway.seniorway.entity.schedule.ScheduleTouristSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleTouristSpotRepository extends JpaRepository<ScheduleTouristSpotEntity, Long> {
    // schedule_id로 조회
    List<ScheduleTouristSpotEntity> findBySchedule_ScheduleId(Long scheduleId);

    @Query("""
      select new com.seniorway.seniorway.dto.location.SpotPointDto(
        ts.touristSpotId, ts.mapx, ts.mapy, sts.sequenceOrder
      )
      from ScheduleTouristSpotEntity sts
      join sts.touristSpot ts
      where sts.schedule.scheduleId = :scheduleId
      order by sts.sequenceOrder
    """)
    List<SpotPointDto> findPointsByScheduleId(Long scheduleId);
}

