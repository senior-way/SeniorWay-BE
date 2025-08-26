package com.seniorway.seniorway.entity.location;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "guardian_location_view")
public class GuardianLocationView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long GuardianLocationViewId;

    private Long guardianId;

    private Long userLocationId;

    private LocalDateTime viewedAt;
}
