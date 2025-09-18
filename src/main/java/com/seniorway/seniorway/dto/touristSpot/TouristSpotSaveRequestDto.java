package com.seniorway.seniorway.dto.touristSpot;

import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TouristSpotSaveRequestDto {
    private String contentId;
    private String contentTypeId;
    private String title;
    private String tel;
    private String zipcode;
    private String addr1;
    private String addr2;
    private String areacode;
    private String sigungucode;
    private String mapx;
    private String mapy;
    private String mlevel;
    private String firstimage;
    private String firstimage2;
    private String cat1;
    private String cat2;
    private String cat3;
    private String lclsSystm1;
    private String lclsSystm2;
    private String lclsSystm3;
    private String lDongRegnCd;
    private String lDongSignguCd;
    private String cpyrhtDivCd;
    // createdTime, modifiedTime은 자동 관리 필드로 제외

    @Builder
    public TouristSpotSaveRequestDto(String contentId, String contentTypeId, String title, String tel, String zipcode,
                                    String addr1, String addr2, String areacode, String sigungucode,
                                    String mapx, String mapy, String mlevel, String firstimage, String firstimage2,
                                    String cat1, String cat2, String cat3, String lclsSystm1, String lclsSystm2,
                                    String lclsSystm3, String lDongRegnCd, String lDongSignguCd, String cpyrhtDivCd) {
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
        this.title = title;
        this.tel = tel;
        this.zipcode = zipcode;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.areacode = areacode;
        this.sigungucode = sigungucode;
        this.mapx = mapx;
        this.mapy = mapy;
        this.mlevel = mlevel;
        this.firstimage = firstimage;
        this.firstimage2 = firstimage2;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.lclsSystm1 = lclsSystm1;
        this.lclsSystm2 = lclsSystm2;
        this.lclsSystm3 = lclsSystm3;
        this.lDongRegnCd = lDongRegnCd;
        this.lDongSignguCd = lDongSignguCd;
        this.cpyrhtDivCd = cpyrhtDivCd;
    }

    public TouristSpotEntity toEntity() {
        return TouristSpotEntity.builder()
                .contentId(contentId)
                .contentTypeId(contentTypeId)
                .title(title)
                .tel(tel)
                .zipcode(zipcode)
                .addr1(addr1)
                .addr2(addr2)
                .areacode(areacode)
                .sigungucode(sigungucode)
                .mapx(mapx)
                .mapy(mapy)
                .mlevel(mlevel)
                .firstimage(firstimage)
                .firstimage2(firstimage2)
                .cat1(cat1)
                .cat2(cat2)
                .cat3(cat3)
                .lclsSystm1(lclsSystm1)
                .lclsSystm2(lclsSystm2)
                .lclsSystm3(lclsSystm3)
                .lDongRegnCd(lDongRegnCd)
                .lDongSignguCd(lDongSignguCd)
                .cpyrhtDivCd(cpyrhtDivCd)
                .build();
    }
}
