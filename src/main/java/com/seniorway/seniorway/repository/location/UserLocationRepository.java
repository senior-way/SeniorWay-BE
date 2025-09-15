package com.seniorway.seniorway.repository.location;

import com.seniorway.seniorway.entity.location.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    List<UserLocation> findTop10ByUserIdOrderByTimestampDesc(Long userId);

    Optional<UserLocation> findTopByUserIdOrderByTimestampDesc(Long userId);
}
