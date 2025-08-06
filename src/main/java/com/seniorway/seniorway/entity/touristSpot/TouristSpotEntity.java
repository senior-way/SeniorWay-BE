package com.seniorway.seniorway.entity.touristSpot;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "tourist_spot")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TouristSpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourist_spot_id")
    private Long touristSpotId;

    @Column(name = "content_id", nullable = false, length = 50)
    private String contentId;

    @Column(name = "content_type_id", length = 20)
    private String contentTypeId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "tel", length = 100)
    private String tel;

    @Column(name = "zipcode", length = 20)
    private String zipcode;

    @Column(name = "addr1", length = 255)
    private String addr1;

    @Column(name = "addr2", length = 255)
    private String addr2;

    @Column(name = "areacode", length = 20)
    private String areacode;

    @Column(name = "sigungucode", length = 20)
    private String sigungucode;

    @Column(name = "mapx", length = 50)
    private String mapx;

    @Column(name = "mapy", length = 50)
    private String mapy;

    @Column(name = "mlevel", length = 10)
    private String mlevel;

    @Column(name = "firstimage", columnDefinition = "TEXT")
    private String firstimage;

    @Column(name = "firstimage2", columnDefinition = "TEXT")
    private String firstimage2;

    @Column(name = "cat1", length = 20)
    private String cat1;

    @Column(name = "cat2", length = 20)
    private String cat2;

    @Column(name = "cat3", length = 20)
    private String cat3;

    @Column(name = "lcls_systm1", length = 100)
    private String lclsSystm1;

    @Column(name = "lcls_systm2", length = 100)
    private String lclsSystm2;

    @Column(name = "lcls_systm3", length = 100)
    private String lclsSystm3;

    @Column(name = "lDongRegnCd", length = 20)
    private String lDongRegnCd;

    @Column(name = "lDongSignguCd", length = 20)
    private String lDongSignguCd;

    @Column(name = "cpyrhtDivCd", length = 20)
    private String cpyrhtDivCd;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    // ...getter/setter, 생성자 등...
}
