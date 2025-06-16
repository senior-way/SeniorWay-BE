package com.seniorway.seniorway.client;

import com.seniorway.seniorway.config.TourGuideGreenTour.TourGuideGreenTourApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class TourGuideGreenTourApiClient {
    private final WebClient webClient;
    private final TourGuideGreenTourApiProperties properties;
    private static final String BASE_URL = "https://apis.data.go.kr/B551011/GreenTourService1";

    public TourGuideGreenTourApiClient(WebClient webClient, TourGuideGreenTourApiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public String fetchGreenTourData(String url) {
        System.out.println("serviceKey: " + properties.getServiceKey());

        String baseUrl = "https://apis.data.go.kr/B551011/GreenTourService1";

        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(url)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "SeniorWayApp")
                .queryParam("_type", "json")
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("serviceKey", properties.getServiceKey())
                .build(true) // 자동 인코딩
                .toUri();

        System.out.println("Built URI: " + uri);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
