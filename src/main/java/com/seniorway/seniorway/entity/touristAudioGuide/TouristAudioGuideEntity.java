package com.seniorway.seniorway.entity.touristAudioGuide;

import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tourist_audio_guides")
public class TouristAudioGuideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_spot_id", nullable = false)
    private TouristSpotEntity touristSpot;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "map_x", length = 50)
    private String mapX;

    @Column(name = "map_y", length = 50)
    private String mapY;

    @Column(name = "audio_title", length = 255)
    private String audioTitle;

    @Column(name = "script", columnDefinition = "TEXT")
    private String script;

    @Column(name = "play_time", length = 50)
    private String playTime;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "lang_code", length = 10)
    private String langCode;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ...getter/setter, 생성자 등...
}
