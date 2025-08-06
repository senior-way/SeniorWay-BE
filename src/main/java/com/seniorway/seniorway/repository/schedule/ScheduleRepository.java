package com.seniorway.seniorway.repository.schedule;

import com.seniorway.seniorway.entity.schedule.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    // user_id로 일정 리스트 조회
    List<ScheduleEntity> findByUserId(Long userId);
}

