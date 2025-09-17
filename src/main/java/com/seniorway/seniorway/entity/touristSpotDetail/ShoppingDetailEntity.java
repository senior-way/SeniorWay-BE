package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;

@Entity
@Table(name = "Shopping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingDetailEntity {

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

    @Column(length = 400)
    private String saleItem;

    @Column(length = 255)
    private String saleItemCost;

    @Column(length = 100)
    private String fairDay;

    @Column(length = 50)
    private String openDate;

    @Lob
    private String shopGuide;

    @Lob
    private String cultureCenter;

    @Column(length = 10)
    private String restroomAvailable;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 100)
    private String scale;

    @Column(length = 200)
    private String restDate;

    @Column(length = 200)
    private String parkingAvailable;

    @Column(length = 10)
    private String chkBabyCarriage;

    @Column(length = 10)
    private String chkPet;

    @Column(length = 200)
    private String chkCreditCard;

    @Column(length = 100)
    private String openTime;
}
