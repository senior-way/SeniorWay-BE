package com.seniorway.seniorway.service.alarm;

import com.seniorway.seniorway.dto.location.SpotPointDto;
import com.seniorway.seniorway.entity.location.UserLocation;
import com.seniorway.seniorway.entity.schedule.ScheduleEntity;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import com.seniorway.seniorway.repository.location.UserLocationRepository;
import com.seniorway.seniorway.repository.schedule.ScheduleRepository;
import com.seniorway.seniorway.repository.schedule.ScheduleTouristSpotRepository;
import com.seniorway.seniorway.repository.user.UserGuardianLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private static final double NEAR_THRESHOLD_M = 500.0;
    private static final Duration THROTTLE_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleTouristSpotRepository scheduleTouristSpotRepository;
    private final UserGuardianLinkRepository userGuardianLinkRepository;
    private final UserLocationRepository userLocationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    //현재 위치와 관광지 위치 비교해서 메일 발송
    @Override
    public void sendMail(Long userId){
        // 유저 최신 위치
        Optional<UserLocation> latestOpt = userLocationRepository.findTopByUserIdOrderByTimestampDesc(userId);
        if (latestOpt.isEmpty()) {
            log.warn("No latest UserLocation for userId={}", userId);
            return;
        }
        UserLocation latest = latestOpt.get();
        double userLat = latest.getLatitude();
        double userLon = latest.getLongitude();

        // 유저 스케줄별 관광지 좌표 조회
        List<ScheduleEntity> scheduleList = scheduleRepository.findByUserId(userId);
        boolean isNearAnySpot = false;
        String nearMsg = null;

        String throttleKey = null;

        for (ScheduleEntity schedule : scheduleList) {
            List<SpotPointDto> points = scheduleTouristSpotRepository.findPointsByScheduleId(schedule.getScheduleId());
            for (SpotPointDto p : points) {
                Double spotLon = parseDoubleSafe(p.getMapX());
                Double spotLat = parseDoubleSafe(p.getMapY());
                if (spotLon == null || spotLat == null) continue;

                double distM = distanceMeters(userLat, userLon, spotLat, spotLon);
                if (distM <= NEAR_THRESHOLD_M) {

                    Long spotId = p.getTouristSpotId();
                    if (spotId == null) continue;

                    throttleKey = "mail:proximity:spot:" + userId + ":" + spotId;

                    Boolean acquired = redisTemplate.opsForValue()
                            .setIfAbsent(throttleKey, "1", THROTTLE_TTL);
                    if (!Boolean.TRUE.equals(acquired)) {
                        log.info("Skip mail (throttled): userId={} spotId={} TTL={}m",
                                userId, spotId, THROTTLE_TTL.toMinutes());
                        return;
                    }

                    isNearAnySpot = true;
                    int seq = (p.getSequenceOrder() == null ? 0 : p.getSequenceOrder());
                    nearMsg = String.format(
                            "스케줄ID %d / 관광지ID %d (순서 %d)와 %.0f m 이내",
                            schedule.getScheduleId(), spotId, seq, distM
                    );
                    break;
                }
            }
            if (isNearAnySpot) break;
        }

        if (!isNearAnySpot) {
            log.info("userId={} : 가까운 관광지 없음(> {} m)", userId, NEAR_THRESHOLD_M);
            return;
        }

        // 보호자 목록에게 메일 발송
        List<UserGuardianLinkEntity> guardianList = userGuardianLinkRepository.findByUserId(userId);
        if (guardianList == null || guardianList.isEmpty()) {
            if (throttleKey != null) {
                redisTemplate.delete(throttleKey);
                log.info("No guardians. throttle key cleared: {}", throttleKey);
            }
            return;
        }

        int success = 0;
        for (UserGuardianLinkEntity link : guardianList) {
            User guardian = link.getGuardian();
            if (guardian == null || guardian.getEmail() == null) continue;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(guardian.getEmail());
            message.setSubject("[SeniorWay] 피보호자 위치 알림");
            message.setText(
                    "피보호자분의 현재 위치가 등록된 관광지 반경 500m 이내입니다.\n\n" +
                            "정보: " + nearMsg + "\n" +
                            String.format("현재 좌표: lat=%.6f, lon=%.6f\n", userLat, userLon) +
                            "확인 부탁드립니다."
            );
            try {
                mailSender.send(message);
                success++;
                log.info("Guardian mail sent to {}", guardian.getEmail());
            } catch (Exception e) {
                log.error("Failed to send mail to {}", guardian.getEmail(), e);
            }
        }
        if (success == 0 && throttleKey != null) {
            redisTemplate.delete(throttleKey);
            log.info("All mails failed. throttle key cleared: {}", throttleKey);
        }
    }

    // 테스트용
    @Override
    public void sendTestMail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[SeniorWay] 테스트 메일");
        message.setText("이것은 테스트 이메일 받았다면 정상 작동중.");

        mailSender.send(message);
    }

    // 초대 메일 발송
    @Override
    public void sendInvite(Long wardUserId, String guardianEmail) {

    }

    // 문자열 좌표 안전 파싱
    private Double parseDoubleSafe(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    // 하버사인: WGS84 기준 거리(m)
    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // 지구 반경(m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

}
