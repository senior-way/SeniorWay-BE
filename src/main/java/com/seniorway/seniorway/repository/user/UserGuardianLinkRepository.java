package com.seniorway.seniorway.repository.user;

import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGuardianLinkRepository extends JpaRepository<UserGuardianLinkEntity, Long> {
    List<UserGuardianLinkEntity> findByUserId(Long userId);

    List<UserGuardianLinkEntity> findByGuardianId(Long guardianId);

    // 특정 피보호자-보호자 관계 존재 여부
    boolean existsByUserIdAndGuardianId(Long userId, Long guardianId);

    Optional<UserGuardianLinkEntity> findByUserIdAndGuardianId(Long userId, Long guardianId);

    // 관계 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM UserGuardianLinkEntity ugl WHERE ugl.user.id = :userId AND ugl.guardian.id = :guardianId")
    void deleteByUserIdAndGuardianId(@Param("userId") Long userId, @Param("guardianId") Long guardianId);

    // 피보호자의 관계 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM UserGuardianLinkEntity ugl WHERE ugl.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 보호자의 관계 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM UserGuardianLinkEntity ugl WHERE ugl.guardian.id = :guardianId")
    void deleteByGuardianId(@Param("guardianId") Long guardianId);
}

