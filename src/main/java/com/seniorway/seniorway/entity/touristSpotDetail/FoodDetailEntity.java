package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Food")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDetailEntity {

    @Id
    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "content_type_id", nullable = false)
    private Integer contentTypeId;

    @Column(length = 50)
    private String seatInfo;

    private Integer kidsFacility;

    @Column(length = 100)
    private String firstMenu;

    @Lob
    private String treatMenu;

    @Column(length = 10)
    private String smokingAllowed;

    @Column(length = 10)
    private String packingAvailable;

    @Column(length = 50)
    private String infoCenter;

    @Column(length = 100)
    private String scale;

    @Column(length = 50)
    private String parkingAvailable;
}
