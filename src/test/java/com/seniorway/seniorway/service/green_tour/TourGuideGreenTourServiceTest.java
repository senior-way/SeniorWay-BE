package com.seniorway.seniorway.service.green_tour;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TourGuideGreenTourServiceTest {

    @Autowired
    private TourGuideGreenTourService greenTourService;

    @Test
    void testGetAllGreenTourData() {
        String url = "areaBasedList1";
        String result = greenTourService.getGreenTourData(url);
        System.out.println("\n===== API 응답 결과 (일부) =====");
        System.out.println(result.substring(0, Math.min(result.length(), 1000)));
        System.out.println("=================================\n");
    }
}