package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import lombok.*;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;

@Entity
@Table(name = "festival")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 내부 PK

    @Column(name = "content_id", nullable = false, unique = true)
    private String contentId; // API 고유 ID

    @Column(name = "content_type_id")
    private String contentTypeId; // 콘텐츠 유형 ID

    @Column(name = "sponsor1")
    private String sponsor1; // 주최1

    @Column(name = "sponsor1_tel")
    private String sponsor1Tel; // 주최1 전화번호

    @Column(name = "sponsor2")
    private String sponsor2; // 주최2

    @Column(name = "sponsor2_tel")
    private String sponsor2Tel; // 주최2 전화번호

    @Column(name = "event_start_date")
    private String eventStartDate; // 행사 시작일 (yyyymmdd)

    @Column(name = "event_end_date")
    private String eventEndDate; // 행사 종료일 (yyyymmdd)

    @Column(name = "play_time")
    private String playTime; // 공연/행사 시간

    @Column(name = "event_place")
    private String eventPlace; // 행사 장소

    @Column(name = "event_homepage")
    private String eventHomepage; // 홈페이지

    @Column(name = "age_limit")
    private String ageLimit; // 연령 제한

    @Column(name = "booking_place")
    private String bookingPlace; // 예매처

    @Column(name = "place_info", columnDefinition = "TEXT")
    private String placeInfo; // 장소 정보

    @Column(name = "sub_event", columnDefinition = "TEXT")
    private String subEvent; // 부 행사

    @Column(name = "program", columnDefinition = "TEXT")
    private String program; // 프로그램 소개

    @Column(name = "use_time_festival")
    private String useTimeFestival; // 이용 요금

    @Column(name = "discount_info_festival")
    private String discountInfoFestival; // 할인 정보

    @Column(name = "spend_time_festival")
    private String spendTimeFestival; // 소요 시간

    @Column(name = "festival_grade")
    private String festivalGrade; // 축제 등급

    @Column(name = "progress_type")
    private String progressType; // 진행 방식

    @Column(name = "festival_type")
    private String festivalType; // 축제 유형

    // TouristSpotEntity와의 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "content_id", referencedColumnName = "content_id", insertable = false, updatable = false),
        @JoinColumn(name = "content_type_id", referencedColumnName = "content_type_id", insertable = false, updatable = false)
    })
    private TouristSpotEntity touristSpot;
}
