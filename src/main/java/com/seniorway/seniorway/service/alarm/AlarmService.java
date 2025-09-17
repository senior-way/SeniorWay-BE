package com.seniorway.seniorway.service.alarm;

public interface AlarmService {

    public void sendMail(Long userId);

    public void sendTestMail(String toEmail);

    public void sendInvite(Long wardUserId, String guardianEmail);

    public void accept(String token, Long guardianUserId);
}
