package com.seniorway.seniorway.client;

import com.seniorway.seniorway.config.TourGuideGreenTour.TourGuideGreenTourApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class TourGuideGreenTourApiClient {
    private final WebClient webClient;
    private final TourGuideGreenTourApiProperties properties;

    public TourGuideGreenTourApiClient(WebClient webClient, TourGuideGreenTourApiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public String fetchGreenTourData(String url) {
        System.out.println("serviceKey: " + properties.getServiceKey());
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(url)
                        .queryParam("serviceKey", properties.getServiceKey())
                        .queryParam("MobileOS", "ETC") // 요구되는 기본 파라미터
                        .queryParam("MobileApp", "SeniorWayApp")
                        .queryParam("_type", "json") // json 형식 요청
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo", 1)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
