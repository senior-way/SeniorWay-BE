package com.seniorway.seniorway.repository.user;

import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGuardianLinkRepository extends JpaRepository<UserGuardianLinkEntity, Long> {
    List<UserGuardianLinkEntity> findByUserId(Long userId);
    List<UserGuardianLinkEntity> findByGuardianId(Long guardianId);
}

