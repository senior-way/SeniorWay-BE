package com.seniorway.seniorway.service.schedule;

import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;

public interface ScheduleService {
    String generateSchedulePrompt(SchedulePromptRequestDto requestDto, String userEmail);
}