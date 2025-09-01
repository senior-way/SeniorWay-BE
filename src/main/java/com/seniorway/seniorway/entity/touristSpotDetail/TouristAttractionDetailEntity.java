package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TouristAttraction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristAttractionDetailEntity {

    @Id
    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "content_type_id", nullable = false)
    private Integer contentTypeId;

    private Integer heritage1;
    private Integer heritage2;
    private Integer heritage3;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 50)
    private String openDate;

    @Column(length = 50)
    private String restDate;

    @Lob
    private String expGuide;

    @Column(length = 50)
    private String expAgeRange;

    private Integer accomCount;

    @Column(length = 50)
    private String useSeason;

    @Column(length = 50)
    private String useTime;

    @Column(length = 100)
    private String parkingAvailable;

    @Column(length = 10)
    private String chkBabyCarriage;

    @Column(length = 10)
    private String chkPet;

    @Column(length = 10)
    private String chkCreditCard;
}