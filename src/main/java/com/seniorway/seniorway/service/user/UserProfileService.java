package com.seniorway.seniorway.service.user;

import com.seniorway.seniorway.dto.user.UserProfileRequestDto;
import com.seniorway.seniorway.entity.User;
import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.repository.profile.UserProfileRepository;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveOrUpdateUserProfile(Long userId, UserProfileRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElse(UserProfileEntity.builder().user(user).build());

        profile.setPreferredCategory(dto.getPreferredCategory());
        profile.setPreferredTransportation(dto.getPreferredTransportation());
        profile.setWheelchairUsage(dto.getWheelchairUsage());
        profile.setPetCompanion(dto.getPetCompanion());
        profile.setDigitalLiteracy(dto.getDigitalLiteracy());

        userProfileRepository.save(profile);
    }
}
