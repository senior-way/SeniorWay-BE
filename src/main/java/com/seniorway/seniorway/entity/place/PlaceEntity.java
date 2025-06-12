package com.seniorway.seniorway.entity.place;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(length = 50, nullable = false)
    private String region;

    @Column(name = "is_barrier_free", nullable = false)
    private Boolean isBarrierFree;

    @Column(length = 255, nullable = false)
    private String tags;

    @Column(length = 1000, name = "image_url", nullable = false)
    private String imageUrl;

    @Column(length = 1000, name = "audio_guide_url", nullable = false)
    private String audioGuideUrl;

    @Column(length = 1000, name = "pdf_url", nullable = false)
    private String pdfUrl;
}