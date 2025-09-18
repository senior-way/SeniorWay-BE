package com.seniorway.seniorway.controller.alarm;

import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.alarm.AlarmService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @PostMapping("/guardian/invite")
    public ResponseEntity<?> invite(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestParam String wardEmail,
                                    @RequestParam(required = false) String wardName) {
        Long guardianUserId = customUserDetails.getUserId();
        alarmService.sendInvite(guardianUserId, wardEmail, wardName); // 토큰 생성 + 메일 발송
        return ResponseEntity.ok("초대 메일을 전송하였습니다.");
    }

    @PostMapping("/guardian/accept")
    public ResponseEntity<?> accept(@RequestParam("token") @NotBlank String token,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("accept token={}", token);
        Long wardUserId = customUserDetails.getUserId();
        alarmService.accept(token, wardUserId);
        return ResponseEntity.ok("초대에 수락하였습니다.");
    }
}
