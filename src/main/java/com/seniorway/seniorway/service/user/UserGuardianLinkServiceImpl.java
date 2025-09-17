package com.seniorway.seniorway.service.user;

import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.entity.user.UserGuardianLinkEntity;
import com.seniorway.seniorway.enums.error.ErrorCode;
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
        User user = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User guardian = userService.getUserById(guardianId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserGuardianLinkEntity link = userGuardianLinkRepository.save(UserGuardianLinkEntity.builder()
                        .user(user)
                        .guardian(guardian)
                .build());

        userGuardianLinkRepository.save(link);
    }
}
