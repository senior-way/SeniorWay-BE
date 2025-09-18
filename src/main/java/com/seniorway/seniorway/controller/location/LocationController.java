package com.seniorway.seniorway.controller.location;

import com.seniorway.seniorway.dto.location.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;
import com.seniorway.seniorway.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    /**
     * -----------------------------
     * WebSocket(STOMP) 전송용
     * -----------------------------
     * 클라이언트로부터 위치 메시지를 수신하고 위치 서비스에 위임하여 처리합니다.
     * 메시지에는 위도, 경도, 타임스탬프와 같은 사용자별 위치 데이터가 포함됩니다.
     * 인증된 사용자의 상세 정보에서 사용자 ID가 메시지에 할당됩니다.
     *
     * @param msg         위도, 경도, 타임스탬프 및 선택적으로 사용자 ID가 포함된 위치 메시지
     * @param userDetails 사용자 ID, 이메일 및 역할 정보를 포함한 인증된 사용자의 상세 정보
     */
    @MessageMapping("/location")  // client -> server
    public void receiveLocation(LocationMessage msg,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        msg.setUserId(userDetails.getUserId());
        locationService.handleLocation(msg, userDetails);
    }

    /**
     * -----------------------------
     * REST API 전송용
     * -----------------------------
     * 클라이언트의 위치 메시지를 처리하고 전송합니다. 위치 데이터(위도, 경도, 타임스탬프)와
     * 사용자 상세 정보를 처리하고, 해당 위치 서비스를 트리거하여 데이터를 처리합니다.
     *
     * @param msg         위도, 경도, 타임스탬프 및 선택적으로 사용자 ID가 포함된 위치 메시지
     * @param userDetails 사용자 ID, 이메일 및 역할 정보를 포함한 인증된 사용자의 상세 정보
     * @return 일반적으로 HTTP 200 OK 응답인 요청 결과를 나타내는 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<?> sendLocation(
            @RequestBody LocationMessage msg,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        msg.setUserId(userDetails.getUserId());
        locationService.handleLocation(msg, userDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * 보호된 사용자의 최근 위치를 가져옵니다.
     * 로그인한 보호자(Guardian)의 ID를 사용하여, 해당 보호자가 관리하는
     * 피보호자의 최근 위치를 조회합니다.
     *
     * @param userDetails 보호자(Guardian)의 사용자 정보
     * @return 보호된 사용자의 최근 위치를 담은 LocationMessage
     */
    @GetMapping("/protected")
    public ResponseEntity<LocationMessage> getProtectedUserLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LocationMessage location = locationService.getProtectedUserLastLocation(userDetails.getUserId());
        if (location == null) {
            // location = new LocationMessage(3L, 37.44, 126.88, null);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(location);
    }
}