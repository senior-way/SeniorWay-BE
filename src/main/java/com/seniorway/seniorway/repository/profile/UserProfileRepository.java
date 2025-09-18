package com.seniorway.seniorway.repository.profile;

import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUser(User user);
    Optional<UserProfileEntity> findByUserId(Long userId);
}
