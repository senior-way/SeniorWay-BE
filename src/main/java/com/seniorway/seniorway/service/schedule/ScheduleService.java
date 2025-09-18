package com.seniorway.seniorway.service.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;

public interface ScheduleService {
    JsonNode generateSchedulePrompt(SchedulePromptRequestDto requestDto, String userEmail);

    // 일정 저장 메서드: 시작일, 끝나는날 제거
    void saveSchedule(String userEmail, String title, JsonNode scheduleJson);

    // 저장된 일정 JSON 반환 메서드
    JsonNode getScheduleJson(Long scheduleId, String userEmail);

    // 일정 목록 조회
    Object getScheduleList(String userEmail);

    // 일정 삭제
    void deleteSchedule(Long scheduleId, String userEmail);
}
