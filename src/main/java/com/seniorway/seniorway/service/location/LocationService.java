package com.seniorway.seniorway.service.location;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;

public interface LocationService {

    /**
     * 위치 메시지를 처리합니다. 위치 데이터를 저장하고 관련 이벤트를 발행하며
     * 지정된 수신자에게 위치 정보를 전송합니다.
     *
     * @param msg         사용자의 위도, 경도, 타임스탬프와 같은 위치 데이터가 포함된 위치 메시지
     * @param userDetails 사용자 ID, 이메일, 역할을 포함한 사용자 상세 정보
     */
    void handleLocation(LocationMessage msg, CustomUserDetails userDetails);
}
