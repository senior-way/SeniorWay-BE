package com.seniorway.seniorway.controller.alarm;

import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.alarm.AlarmService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알람 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMailToGuardian(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        alarmService.sendMail(customUserDetails.getUserId());
        return ResponseEntity.ok("보호자에게 메일을 전송하였습니다.");
    }

    @PostMapping("/test")
    public ResponseEntity<?> sendTestEmail(@RequestParam("email") String toEmail){
        alarmService.sendTestMail(toEmail);
        return ResponseEntity.ok("테스트 메일을 전송하였습니다.");
    }
}
