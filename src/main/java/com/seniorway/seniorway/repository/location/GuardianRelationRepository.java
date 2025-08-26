package com.seniorway.seniorway.repository.location;

import com.seniorway.seniorway.entity.location.GuardianRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRelationRepository extends JpaRepository<GuardianRelation, Long> {

    boolean existsByGuardianIdAndWardId(Long guardianId, Long wardId);
}
