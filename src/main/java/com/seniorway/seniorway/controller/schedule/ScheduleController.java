package com.seniorway.seniorway.controller.schedule;

import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;
import com.seniorway.seniorway.dto.schedule.SchedulePromptResponseDto;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/prompt")
    public ResponseEntity<SchedulePromptResponseDto> createSchedulePrompt(
            @RequestBody SchedulePromptRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        String prompt = scheduleService.generateSchedulePrompt(requestDto, userEmail);
        SchedulePromptResponseDto responseDto = new SchedulePromptResponseDto(prompt);

        return ResponseEntity.ok(responseDto);
    }
}