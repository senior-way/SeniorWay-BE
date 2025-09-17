package com.seniorway.seniorway.repository.user;

import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGuardianLinkRepository extends JpaRepository<UserGuardianLinkEntity, Long> {
    List<UserGuardianLinkEntity> findByUserId(Long userId);
    List<UserGuardianLinkEntity> findByGuardianId(Long guardianId);

    boolean existsByUser_IdAndGuardian_Id(Long userId, Long guardianId);
}

