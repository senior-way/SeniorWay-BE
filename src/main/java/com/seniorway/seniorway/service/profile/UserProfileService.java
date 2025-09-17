package com.seniorway.seniorway.service.profile;

import com.seniorway.seniorway.dto.profile.UserProfileRequestDto;
import com.seniorway.seniorway.entity.profile.DigitalLiteracyLevel;
import com.seniorway.seniorway.entity.profile.UserProfileEntity;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.repository.profile.UserProfileRepository;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserProfileEntity createOrUpdateProfile(Long userId, UserProfileRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileEntity userProfile = userProfileRepository.findByUser(user)
                .orElse(new UserProfileEntity());

        userProfile.setUser(user);
        userProfile.setPreferredCategories(Arrays.asList(requestDto.getPreferredCategory().split(",")));
        userProfile.setPreferredTransportations(Arrays.asList(requestDto.getPreferredTransportation().split(",")));
        userProfile.setWheelchairUsage(requestDto.getWheelchairUsage() == 1);
        userProfile.setPetCompanion(requestDto.getPetCompanion() == 1);

        DigitalLiteracyLevel level;
        switch (requestDto.getDigitalLiteracy()) {
            case "상":
                level = DigitalLiteracyLevel.HIGH;
                break;
            case "중":
                level = DigitalLiteracyLevel.MEDIUM;
                break;
            case "하":
                level = DigitalLiteracyLevel.LOW;
                break;
            default:
                throw new IllegalArgumentException("Invalid digital literacy level: " + requestDto.getDigitalLiteracy());
        }
        userProfile.setDigitalLiteracy(level);

        return userProfileRepository.save(userProfile);
    }

    public UserProfileEntity getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserProfile not found for user " + userId));
    }

    public List<UserProfileEntity> getAll() {
        return userProfileRepository.findAll();
    }

    @Transactional
    public void deleteProfile(Long userId) {
        UserProfileEntity userProfile = getProfile(userId);
        userProfileRepository.delete(userProfile);
    }
}
