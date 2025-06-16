package com.seniorway.seniorway.service.green_tour;

import com.seniorway.seniorway.client.TourGuideGreenTourApiClient;
import org.springframework.stereotype.Service;

@Service
public class TourGuideGreenTourService {
    private final TourGuideGreenTourApiClient apiClient;

    public TourGuideGreenTourService(TourGuideGreenTourApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public String getGreenTourData(String url) {
        return apiClient.fetchGreenTourData(url);
    }
}
