package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import com.seniorway.seniorway.entity.common.BaseTimeEntity;
import lombok.*;

@Entity
@Table(name = "wheelchair_access_info", uniqueConstraints = @UniqueConstraint(name = "uk_content", columnNames = "content_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WheelchairAccessEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false, length = 50)
    private String contentId;

    @Column(name = "parking", length = 100)
    private String parking;

    @Column(name = "route", length = 100)
    private String route;

    @Column(name = "exit_info", length = 100)
    private String exitInfo;

    @Column(name = "elevator", length = 100)
    private String elevator;

    @Column(name = "restroom", length = 100)
    private String restroom;

    @Column(name = "is_barrier_free")
    private Boolean barrierFree;
}
