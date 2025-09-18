package com.seniorway.seniorway.listener.alarm;

import com.seniorway.seniorway.event.location.LocationUpdatedEvent;
import com.seniorway.seniorway.service.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProximityEmailListener {

    private final AlarmService alarmService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onUserLocationUpdated(LocationUpdatedEvent event) {
        try {
            Long userId = event.userId();
            alarmService.sendMail(userId);
        } catch (Exception e) {
            log.error("Failed to send proximity email for userId={}", event.userId(), e);
        }
    }
}
