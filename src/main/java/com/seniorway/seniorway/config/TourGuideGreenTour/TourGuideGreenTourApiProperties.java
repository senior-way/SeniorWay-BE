package com.seniorway.seniorway.config.TourGuideGreenTour;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.green-tour")
@Getter
@Setter
public class TourGuideGreenTourApiProperties {
    private String serviceKey;
}
