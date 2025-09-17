package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;

@Entity
@Table(name = "Food")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDetailEntity {

    @Id
    @Column(name = "content_id")
    private String contentId;

    @Column(name = "content_type_id", nullable = false)
    private String contentTypeId;

    // TouristSpotEntity와의 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "content_id", referencedColumnName = "content_id", insertable = false, updatable = false),
        @JoinColumn(name = "content_type_id", referencedColumnName = "content_type_id", insertable = false, updatable = false)
    })
    private TouristSpotEntity touristSpot;

    @Column(length = 200)
    private String seatInfo;

    @Column(length = 20)
    private String kidsFacility;

    @Column(length = 100)
    private String firstMenu;

    @Lob
    private String treatMenu;

    @Column(length = 200)
    private String smokingAllowed;

    @Column(length = 200)
    private String packingAvailable;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 100)
    private String scale;

    @Column(length = 200)
    private String parkingAvailable;
}
