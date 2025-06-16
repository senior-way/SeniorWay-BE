package com.seniorway.seniorway.config.TourGuideGreenTour;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://apis.data.go.kr/B551011/GreenTourService1")
                .build();
    }
}
