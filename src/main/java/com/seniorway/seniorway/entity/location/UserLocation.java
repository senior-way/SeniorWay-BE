package com.seniorway.seniorway.entity.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocation {

    @Id
    @GeneratedValue()
    private Long UserLocationId;

    private Long guardianId;
    private Long UserId;

    private Double latitude;      // 위도
    private Double longitude;     // 경도

    private LocalDateTime timestamp; // 기록 시간
}
