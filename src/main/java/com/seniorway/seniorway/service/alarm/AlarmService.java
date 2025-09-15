package com.seniorway.seniorway.service.alarm;

public interface AlarmService {

    public void sendMail(Long userId);

    public void sendTestMail(String toEmail);
}
