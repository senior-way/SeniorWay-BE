package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;

@Entity
@Table(name = "TouristAttraction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristAttractionDetailEntity {

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

    private String heritage1;
    private String heritage2;
    private String heritage3;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 50)
    private String openDate;

    @Column(length = 200)
    private String restDate;

    @Lob
    private String expGuide;

    @Column(length = 50)
    private String expAgeRange;

    @Column(length = 400)
    private String accomCount;

    @Column(length = 50)
    private String useSeason;

    @Column(length = 200)
    private String useTime;

    @Column(length = 200)
    private String parkingAvailable;

    @Column(length = 10)
    private String chkBabyCarriage;

    @Column(length = 10)
    private String chkPet;

    @Column(length = 10)
    private String chkCreditCard;
}