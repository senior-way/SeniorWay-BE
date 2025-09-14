package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;

@Entity
@Table(name = "PerformanceExhibition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceExhibitionDetailEntity {

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

    @Column(length = 100)
    private String scale;

    @Column(length = 255)
    private String useFee;

    @Column(length = 255)
    private String discountInfo;

    @Column(length = 50)
    private String spendTime;

    @Column(length = 50)
    private String parkingFee;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 20)
    private String accomCount;

    @Column(length = 50)
    private String useTime;

    @Column(length = 50)
    private String restDate;

    @Column(length = 50)
    private String parkingAvailable;

    @Column(length = 10)
    private String chkBabyCarriage;

    @Column(length = 10)
    private String chkPet;

    @Column(length = 10)
    private String chkCreditCard;
}
