package com.seniorway.seniorway.service.schedule;

import com.seniorway.seniorway.dto.schedule.SchedulePromptRequestDto;
import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.repository.profile.UserProfileRepository;
import com.seniorway.seniorway.repository.touristSpot.TouristSpotRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.PetFriendlyInfoRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.WheelchairAccessRepository;
import com.seniorway.seniorway.repository.user.UserRepository;
import com.seniorway.seniorway.entity.schedule.ScheduleEntity;
import com.seniorway.seniorway.entity.schedule.ScheduleTouristSpotEntity;
import com.seniorway.seniorway.repository.schedule.ScheduleRepository;
import com.seniorway.seniorway.repository.schedule.ScheduleTouristSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TouristSpotRepository touristSpotRepository;
    // 상세 정보 조회를 위해 관련 레포지토리를 주입합니다.
    private final WheelchairAccessRepository wheelchairAccessRepository;
    private final PetFriendlyInfoRepository petFriendlyInfoRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleTouristSpotRepository scheduleTouristSpotRepository;

    @Value("${openai.api-key}")
    private String openaiApiKey;


    @Override
    public JsonNode generateSchedulePrompt(SchedulePromptRequestDto requestDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserProfileEntity userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_PROFILE_NOT_FOUND));

        List<TouristSpotEntity> allSpots = touristSpotRepository.findAll();

        List<String> preferredCategories = userProfile.getPreferredCategories() != null ? userProfile.getPreferredCategories() : Collections.emptyList();
        boolean includeFood = preferredCategories.stream().anyMatch(cat -> cat.contains("먹거리"));
        int days = requestDto.getDays() != null ? requestDto.getDays() : 1;
        int foodCount = includeFood ? days * 4 : days * 3;

        boolean isWheelchairUser = userProfile.isWheelchairUsage();
        boolean isWithPet = userProfile.isPetCompanion();

        // 베리어 프리 관광지 contentId 목록
        List<String> barrierFreeIds = wheelchairAccessRepository.findAll().stream()
                .filter(w -> w.getBarrierFree() != null && w.getBarrierFree())
                .map(w -> w.getContentId())
                .collect(Collectors.toList());
        // 펫 동반 관광지 contentId 목록
        List<String> petFriendlyIds = petFriendlyInfoRepository.findAll().stream()
                .map(p -> p.getContentId())
                .collect(Collectors.toList());

        // 선호 카테고리 "상관없음" 여부
        boolean isNoPreference = preferredCategories.stream().anyMatch(cat -> cat.contains("상관없음"));

        // lcls_systm1 값 기준 필터링
        List<String> lclsTypes = new ArrayList<>();
        if (!isNoPreference) {
            if (preferredCategories.stream().anyMatch(cat -> cat.contains("자연"))) {
                lclsTypes.add("NA");
            }
            if (preferredCategories.stream().anyMatch(cat -> cat.contains("역사") || cat.contains("문화") || cat.contains("예술"))) {
                lclsTypes.add("VE");
                lclsTypes.add("HS");
            }
            if (preferredCategories.stream().anyMatch(cat -> cat.contains("레저") || cat.contains("체험") || cat.contains("액티비티"))) {
                lclsTypes.add("LS");
                lclsTypes.add("EX");
            }
        }

        List<TouristSpotEntity> filteredSpots = allSpots.stream()
            .filter(spot -> {
                String contentId = spot.getContentId();

                // 휠체어 사용자: 베리어 프리 관광지만
                if (isWheelchairUser && !barrierFreeIds.contains(contentId)) return false;
                // 반려동물 동반: 펫 동반 관광지만
                if (isWithPet && !petFriendlyIds.contains(contentId)) return false;

                // 음식점(39)은 항상 포함
                if ("39".equals(spot.getContentTypeId())) return true;

                // 선호 카테고리 "상관없음"이면 전체 관광지 포함
                if (isNoPreference) return true;

                // lcls_systm1 값으로 필터링
                if (lclsTypes.contains(spot.getLclsSystm1())) return true;

                return false;
            })
            .collect(Collectors.toList());

        // 음식점만 추출 후 foodCount만큼 제한
        List<TouristSpotEntity> restaurantSpots = filteredSpots.stream()
            .filter(spot -> "39".equals(spot.getContentTypeId()))
            .collect(Collectors.toList());
        Collections.shuffle(restaurantSpots);
        List<TouristSpotEntity> limitedRestaurants = restaurantSpots.stream()
            .limit(foodCount)
            .collect(Collectors.toList());

        // 음식점 외 관광지
        List<TouristSpotEntity> nonRestaurantSpots = filteredSpots.stream()
            .filter(spot -> !"39".equals(spot.getContentTypeId()))
            .collect(Collectors.toList());

        // 최종 관광지 목록(title, contentId, firstImage)
        List<TouristSpotEntity> finalSpots = new ArrayList<>();
        finalSpots.addAll(nonRestaurantSpots);
        finalSpots.addAll(limitedRestaurants);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음은 시니어세대 사용자의 여행 계획을 위한 정보입니다.\n");
        promptBuilder.append("아래 정보로 여행 일정을 JSON 형식으로만 만들어줘.\n");
        promptBuilder.append("여행 기간: ").append(days).append("박 ").append(days + 1).append("일\n");
        promptBuilder.append("사용자 특성: ");
        if(isWheelchairUser) promptBuilder.append("휠체어 사용자, ");
        if(isWithPet) promptBuilder.append("반려동물 동반, ");
        promptBuilder.append("디지털 활용 능력 ").append(userProfile.getDigitalLiteracy()).append(" 등급\n");
        promptBuilder.append("선호 카테고리: ").append(String.join(", ", preferredCategories)).append("\n");
        promptBuilder.append("추천 관광지 목록(사진 링크 포함):\n");

        finalSpots.forEach(spot -> {
            promptBuilder.append("- ").append(spot.getTitle())
                         .append(" (contentId: ").append(spot.getContentId())
                         .append(", firstImage: ").append(spot.getFirstimage() != null ? spot.getFirstimage() : "없음")
                         .append(")\n");
        });

        promptBuilder.append("\n");
        promptBuilder.append("제약 조건:\n");
        promptBuilder.append("1. 하루 일정은 관광지 3곳 + 음식점 3곳(아침, 점심, 저녁)으로 구성한다.\n");
        promptBuilder.append("2. 단, 사용자의 선호 카테고리에 \"먹거리\"가 포함된 경우에는 관광지 3곳 + 음식점 4곳(아침, 점심, 저녁, 간식)으로 구성한다.\n");
        promptBuilder.append("3. 음식점은 반드시 아침, 점심, 저녁(+ 간식) 시간에 맞춰 배치한다.\n");
        promptBuilder.append("4. 관광지는 이동 동선을 고려하여 효율적으로 순서를 배치한다.\n");
        promptBuilder.append("5. 각 일정 항목은 다음 순서와 형식으로 출력한다:\n");
        promptBuilder.append("   - 시작 시간 → 장소 이름 → contentId → firstImage\n");
        promptBuilder.append("6. 전체 결과는 반드시 JSON 형식으로 출력하며, 제공한 예시와 동일한 구조를 유지한다.\n");
        promptBuilder.append("   (불필요한 텍스트나 설명은 출력하지 않는다.)\n");

        promptBuilder.append("반환 예시\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"day1\": [\n");
        promptBuilder.append("    {\"time\": \"09:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"},\n");
        promptBuilder.append("    {\"time\": \"11:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"},\n");
        promptBuilder.append("    {\"time\": \"13:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"},\n");
        promptBuilder.append("    {\"time\": \"15:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"},\n");
        promptBuilder.append("    {\"time\": \"17:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"},\n");
        promptBuilder.append("    {\"time\": \"19:00\", \"place\": \"관광지 또는 음식점 이름\", \"contentId\": \"관광지 또는 음식점 contentId\", \"firstImage\": \"사진 링크\"}\n");
        promptBuilder.append("  ],\n");
        promptBuilder.append("  \"day2\": [ ... ],\n");
        promptBuilder.append("  \"day3\": [ ... ],\n");
        promptBuilder.append("  \"day4\": [ ... ],\n");
        promptBuilder.append("  \"day5\": [ ... ],\n");
        promptBuilder.append("  \"day6\": [ ... ]\n");
        promptBuilder.append("}\n");
        promptBuilder.append("위 예시에서 ... 부분은 day1과 동일한 형식으로 계속 이어지는 형태임\n");
        promptBuilder.append("반드시 JSON 형식으로만, 다른 설명이나 내용 없이 출력\n");

        String prompt = promptBuilder.toString();
        ObjectMapper mapper = new ObjectMapper();

        // GPT API 호출
        String gptResponse = callGptApi(prompt);

        // JSON만 추출
        int startIdx = gptResponse.indexOf('{');
        int endIdx = gptResponse.lastIndexOf('}');
        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            String jsonOnly = gptResponse.substring(startIdx, endIdx + 1);

            try {
                return mapper.readTree(jsonOnly); // JsonNode를 직접 반환 (firstImage 포함)
            } catch (Exception e) {
                // 파싱 실패 시 에러를 담은 JsonNode 반환
                return mapper.createObjectNode().put("error", "GPT 응답 JSON 파싱에 실패했습니다: " + e.getMessage());
            }
        } else {
            // 올바른 JSON 형식이 아닐 경우 에러를 담은 JsonNode 반환
            return mapper.createObjectNode().put("error", "올바른 JSON 응답이 아닙니다.");
        }
    }

    // GPT API 호출 메서드 추가
    private String callGptApi(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String apiKey = "Bearer " + openaiApiKey;

        RestTemplate restTemplate = new RestTemplate();

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(
                new JSONObject().put("role", "user").put("content", prompt)
        ));
        System.out.println(requestBody.toString());
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            JSONObject responseJson = new JSONObject(response.getBody());
            String content = responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return content;
        } catch (Exception e) {
            // 에러 처리: 필요에 따라 상세 로깅 및 예외 처리
            return "{\"error\": \"GPT API 호출 실패\"}";
        }
    }

    @Override
    public void saveSchedule(String userEmail, String title, JsonNode scheduleJson) {
        // 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 스케줄 엔티티 생성 및 저장
        ScheduleEntity schedule = ScheduleEntity.builder()
                .user(user)
                .title(title)
                .build();
        scheduleRepository.save(schedule);

        // JSON 필드 순회
        Iterator<String> fieldNames = scheduleJson.fieldNames();
        while (fieldNames.hasNext()) {
            String dayKey = fieldNames.next(); // 예: day1, day2
            JsonNode dayArr = scheduleJson.get(dayKey);

            if (dayArr == null || !dayArr.isArray()) {
                continue; // 배열이 아닐 경우 스킵
            }

            for (int i = 0; i < dayArr.size(); i++) {
                JsonNode item = dayArr.get(i);

                if (item == null || !item.hasNonNull("contentId")) {
                    continue; // contentId 없으면 스킵
                }

                String contentId = item.get("contentId").asText();
                String visitTime = item.hasNonNull("time") ? item.get("time").asText() : null;

                TouristSpotEntity spot = touristSpotRepository.findByContentId(contentId);
                if (spot == null) continue;

                ScheduleTouristSpotEntity scheduleSpot = ScheduleTouristSpotEntity.builder()
                        .schedule(schedule)
                        .touristSpot(spot)
                        .sequenceOrder(i + 1)
                        .visitDate(dayKey) // 예: day1
                        .visitTime(visitTime)
                        .build();

                scheduleTouristSpotRepository.save(scheduleSpot);
            }
        }
    }

    @Override
    public JsonNode getScheduleJson(Long scheduleId, String userEmail) {
        // 사용자 인증 및 스케줄 소유권 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        List<ScheduleTouristSpotEntity> spots = scheduleTouristSpotRepository.findBySchedule(schedule);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();

        // day별로 그룹화
        var grouped = spots.stream()
            .collect(Collectors.groupingBy(ScheduleTouristSpotEntity::getVisitDate));

        // day1, day2, ... 순으로 정렬
        List<String> sortedDayKeys = grouped.keySet().stream()
            .sorted((a, b) -> {
                try {
                    int numA = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                    int numB = Integer.parseInt(b.replaceAll("[^0-9]", ""));
                    return Integer.compare(numA, numB);
                } catch (Exception e) {
                    return a.compareTo(b);
                }
            })
            .toList();

        for (String dayKey : sortedDayKeys) {
            List<ScheduleTouristSpotEntity> daySpots = grouped.get(dayKey);
            ArrayNode dayArr = mapper.createArrayNode();
            daySpots.stream()
                .sorted((a, b) -> a.getSequenceOrder().compareTo(b.getSequenceOrder()))
                .forEach(spot -> {
                    ObjectNode item = mapper.createObjectNode();
                    item.put("time", spot.getVisitTime());
                    item.put("place", spot.getTouristSpot().getTitle());
                    item.put("contentId", spot.getTouristSpot().getContentId());
                    item.put("firstImage", spot.getTouristSpot().getFirstimage() != null ? spot.getTouristSpot().getFirstimage() : "");
                    dayArr.add(item);
                });
            result.set(dayKey, dayArr);
        }

        return result;
    }

    @Override
    public Object getScheduleList(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ScheduleEntity> schedules = scheduleRepository.findByUserId(user.getId());

        return schedules.stream()
                .map(s -> {
                    var obj = new java.util.HashMap<String, Object>();
                    obj.put("scheduleId", s.getScheduleId());
                    obj.put("title", s.getTitle());
                    obj.put("createdAt", s.getCreatedTime());

                    // day1 관광지 조회 및 photoUrl 결정
                    List<ScheduleTouristSpotEntity> day1Spots = scheduleTouristSpotRepository.findBySchedule(s).stream()
                        .filter(sts -> "day1".equals(sts.getVisitDate()))
                        .sorted((a, b) -> a.getSequenceOrder().compareTo(b.getSequenceOrder()))
                        .toList();

                    String photoUrl = null;
                    for (ScheduleTouristSpotEntity spot : day1Spots) {
                        String img = spot.getTouristSpot().getFirstimage();
                        if (img != null && !img.isBlank()) {
                            photoUrl = img;
                            break; // 첫 번째로 이미지가 있는 spot을 사용
                        }
                    }
                    if (photoUrl != null) {
                        obj.put("photoUrl", photoUrl);
                    }

                    return obj;
                })
                .toList();
    }

    @Override
    public void deleteSchedule(Long scheduleId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 일정에 속한 관광지 먼저 삭제
        List<ScheduleTouristSpotEntity> spots = scheduleTouristSpotRepository.findBySchedule(schedule);
        scheduleTouristSpotRepository.deleteAll(spots);

        // 일정 삭제
        scheduleRepository.delete(schedule);
    }
}
