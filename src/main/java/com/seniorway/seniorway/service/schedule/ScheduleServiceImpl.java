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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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


    @Override
    public String generateSchedulePrompt(SchedulePromptRequestDto requestDto, String userEmail) {
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

        // 최종 관광지 목록(title만)
        List<String> spotTitles = new ArrayList<>();
        spotTitles.addAll(nonRestaurantSpots.stream().map(TouristSpotEntity::getTitle).collect(Collectors.toList()));
        spotTitles.addAll(limitedRestaurants.stream().map(TouristSpotEntity::getTitle).collect(Collectors.toList()));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("여행 기간: ").append(days).append("박 ").append(days + 1).append("일\n");
        promptBuilder.append("사용자 특성: ");
        if(isWheelchairUser) promptBuilder.append("휠체어 사용자, ");
        if(isWithPet) promptBuilder.append("반려동물 동반, ");
        promptBuilder.append("디지털 활용 능력 ").append(userProfile.getDigitalLiteracy()).append(" 등급\n");
        promptBuilder.append("선호 카테고리: ").append(String.join(", ", preferredCategories)).append("\n");
        promptBuilder.append("추천 관광지 목록:\n");

        spotTitles.forEach(title -> {
            promptBuilder.append("- ").append(title).append("\n");
        });

        promptBuilder.append("\n위 정보를 바탕으로 상세 여행 일정을 계획해줘.");

        return promptBuilder.toString();
    }
}
