package com.seniorway.seniorway.repository.location;

import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRelationRepository extends UserLocationRepository {

    boolean existsByGuardianIdAndWardId(Long guardianId, Long wardId);
}
