package com.seniorway.seniorway.service.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;

public interface ScheduleService {
    JsonNode generateSchedulePrompt(SchedulePromptRequestDto requestDto, String userEmail);
}
