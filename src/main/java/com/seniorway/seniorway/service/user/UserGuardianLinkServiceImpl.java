package com.seniorway.seniorway.service.user;

import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.repository.user.UserGuardianLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGuardianLinkServiceImpl implements UserGuardianLinkService {

    private final UserGuardianLinkRepository userGuardianLinkRepository;
    private final UserService userService;

    @Override
    public void linkGuardianIdToUserEmail(Long guardianId, String userEmail) {
        User user = userService.findUserByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.USER) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }

        User guardian = userService.findUserById(guardianId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (guardian.getRole() != Role.GUARDIANS) {
            throw new CustomException(ErrorCode.INVALID_GUARDIAN_ROLE);
        }

        UserGuardianLinkEntity link = userGuardianLinkRepository.save(UserGuardianLinkEntity.builder()
                        .user(user)
                        .guardian(guardian)
                .build());

        userGuardianLinkRepository.save(link);
    }
}
