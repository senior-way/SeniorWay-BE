package com.seniorway.seniorway.service.webSocket;

import com.seniorway.seniorway.dto.webSocket.LocationMessage;

public interface LocationService {

    void handleLocation(LocationMessage msg, String authHeader);
}
