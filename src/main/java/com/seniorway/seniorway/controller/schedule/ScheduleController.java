package com.seniorway.seniorway.controller.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/prompt")
    public ResponseEntity<JsonNode> createSchedulePrompt(
            @RequestBody SchedulePromptRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        JsonNode responseNode = scheduleService.generateSchedulePrompt(requestDto, userEmail);

        return ResponseEntity.ok(responseNode);
    }

    // 일정 저장 API 추가
    @PostMapping("/save")
    public ResponseEntity<?> saveSchedule(
            @RequestParam String title,
            @RequestBody JsonNode scheduleJson,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        scheduleService.saveSchedule(userEmail, title, scheduleJson);

        return ResponseEntity.ok().build();
    }
}
