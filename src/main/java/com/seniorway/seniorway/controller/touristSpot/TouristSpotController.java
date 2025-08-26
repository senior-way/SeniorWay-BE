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
}

