package com.seniorway.seniorway.entity.profile;

import com.seniorway.seniorway.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
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

    @Column(name = "preffered_category", nullable = false)
    private String preferredCategory;

    @Column(name = "preferred_transportation", nullable = false)
    private String preferredTransportation;

    @Column(name = "wheelchair_usage", nullable = false)
    private Integer wheelchairUsage;

    @Column(name = "pet_companion", nullable = false)
    private Integer petCompanion;

    @Column(name = "digital_literacy", nullable = false)
    private String digitalLiteracy;

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
