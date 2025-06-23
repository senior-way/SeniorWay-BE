package com.seniorway.seniorway.entity.profile;

import com.seniorway.seniorway.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "age_group", nullable = false)
    private String ageGroup;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String mobility;

    @Column(name = "digital_literacy", nullable = false)
    private String digitalLiteracy;

    @Column(name = "has_pet", nullable = false)
    private Boolean hasPet;

    @Column(name = "use_wheelchair", nullable = false)
    private Boolean useWheelchair;
}
