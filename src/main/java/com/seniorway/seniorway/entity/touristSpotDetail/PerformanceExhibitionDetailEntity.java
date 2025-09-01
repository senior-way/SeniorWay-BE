package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PerformanceExhibition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceExhibitionDetailEntity {

    @Id
    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "content_type_id", nullable = false)
    private Integer contentTypeId;

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

    private Integer accomCount;

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
