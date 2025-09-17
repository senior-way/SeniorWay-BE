package com.seniorway.seniorway.dto.profile;

import lombok.Data;

@Data
public class UserProfileRequestDto {
    private String preferredCategory;         // 콤마로 구분된 문자열
    private String preferredTransportation;   // 콤마로 구분된 문자열
    private Integer wheelchairUsage;          // 1 or 0
    private Integer petCompanion;             // 1 or 0
    private String digitalLiteracy;           // '상', '중', '하'
}
