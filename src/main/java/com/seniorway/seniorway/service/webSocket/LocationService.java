package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;
import com.seniorway.seniorway.security.CustomUserDetails;

public interface LocationService {

    void handleLocation(LocationMessage msg, CustomUserDetails userDetails);
}
