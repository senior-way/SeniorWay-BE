package com.seniorway.seniorway.controller.touristSpot;

import com.seniorway.seniorway.service.touristSpot.TouristSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tourist-spot")
@RequiredArgsConstructor
public class TouristSpotController {

    private final TouristSpotService touristSpotService;

    @PostMapping("/update")
    public ResponseEntity<?> updateTouristSpots() {
        try {
            touristSpotService.fetchAndSaveTouristSpots();
            return ResponseEntity.ok("관광지 정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("관광지 정보 저장 중 오류 발생");
        }
    }

    @PostMapping("/update-detail")
    public ResponseEntity<?> updateTouristSpotDetails() {
        try {
            touristSpotService.fetchAndSaveTouristSpotDetails();
            return ResponseEntity.ok("관광지 상세정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("관광지 상세정보 저장 중 오류 발생");
        }
    }

    @PostMapping("/update-wheelchair-access")
    public ResponseEntity<?> updateWheelchairAccessInfo() {
        try {
            touristSpotService.fetchAndSaveWheelchairAccessInfo();
            return ResponseEntity.ok("무장애 여행정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("무장애 여행정보 저장 중 오류 발생");
        }
    }

    @PostMapping("/update-pet-friendly")
    public ResponseEntity<?> updatePetFriendlyInfo() {
        try {
            touristSpotService.fetchAndSavePetFriendlyInfo();
            return ResponseEntity.ok("반려동물 여행정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("반려동물 여행정보 저장 중 오류 발생");
        }
    }

    @GetMapping("/detail/{contentId}")
    public ResponseEntity<?> getTouristSpotDetail(@PathVariable String contentId) {
        try {
            var spot = touristSpotService.findTouristSpotByContentId(contentId);
            if (spot == null) {
                return ResponseEntity.status(404).body("해당 contentId의 관광지 정보가 없습니다.");
            }
            var detailDto = touristSpotService.getTouristSpotDetailDto(contentId, spot.getContentTypeId());
            return ResponseEntity.ok(detailDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("상세정보 조회 중 오류 발생");
        }
    }

    @GetMapping("/barrier-free")
    public ResponseEntity<?> getBarrierFreeTouristSpots() {
        try {
            var spots = touristSpotService.findBarrierFreeTouristSpots();
            // contentId, title, contentTypeId, firstImage만 추출해서 반환
            var result = spots.stream()
                    .map(spot -> {
                        var map = new java.util.HashMap<String, Object>();
                        map.put("contentId", spot.getContentId());
                        map.put("title", spot.getTitle());
                        map.put("contentTypeId", spot.getContentTypeId());
                        map.put("firstImage", spot.getFirstimage());
                        return map;
                    })
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("무장애(barrier free) 관광지 조회 중 오류 발생");
        }
    }
}
