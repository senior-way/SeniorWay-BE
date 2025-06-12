package com.seniorway.seniorway.repository.profile;

import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUser(User user);
}
