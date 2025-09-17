package com.seniorway.seniorway.entity.profile;

import com.seniorway.seniorway.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 복수 선택 (ElementCollection 활용)
    @ElementCollection
    @CollectionTable(name = "user_preferred_categories", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "category")
    private List<String> preferredCategories;

    @ElementCollection
    @CollectionTable(name = "user_preferred_transportations", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "transportation")
    private List<String> preferredTransportations;

    // 단일 선택 (boolean 권장)
    @Column(name = "wheelchair_usage", nullable = false)
    private boolean wheelchairUsage;

    @Column(name = "pet_companion", nullable = false)
    private boolean petCompanion;

    // Enum 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "digital_literacy", nullable = false)
    private DigitalLiteracyLevel digitalLiteracy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
