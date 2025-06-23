package com.seniorway.seniorway.entity.trip;


import com.seniorway.seniorway.entity.place.PlaceEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trip_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private TripPlanEntity trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @Column(name = "visit_order", nullable = false)
    private Integer visitOrder;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    private String note;
}