package com.seniorway.seniorway.service.user;

public interface UserGuardianLinkService {

    void linkGuardianIdToUserEmail(Long guardianId, String userEmail);

    boolean hasWard(Long guardianId);
}