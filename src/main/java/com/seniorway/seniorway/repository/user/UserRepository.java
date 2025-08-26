package com.seniorway.seniorway.repository.user;

import com.seniorway.seniorway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
