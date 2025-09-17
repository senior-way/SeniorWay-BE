package com.seniorway.seniorway.service.auth;

import com.seniorway.seniorway.dto.auth.GuardianSignUpRequestsDTO;
import com.seniorway.seniorway.enums.error.ErrorCode;
import com.seniorway.seniorway.enums.user.Role;
import com.seniorway.seniorway.exception.CustomException;
import com.seniorway.seniorway.jwt.JwtTokenProvider;
import com.seniorway.seniorway.dto.auth.UserLoginRequestsDTO;
import com.seniorway.seniorway.dto.auth.UserSignUpRequestsDTO;
import com.seniorway.seniorway.entity.user.User;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Spring Security 에서 로그인 시 사용자의 인증 정보를 가져오는 로직
     * 주어진 username 을 기반으로 DB에서 사용자 정보를 조회하고,
     * 조회된 정보를 Spring Security의 UserDetails 객체로 변환하여 반환
     * @param username 로그인 시 username
     * @return UserDetails Spring Security 에서 인증 처리를 위해 사용하는 사용자 정보 객체
     * @throws UsernameNotFoundException username이 DB 에 존재하지 않을 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey()))
        );
    }

    /**
     * 주어진 이메일이 DB 에 이미 존재하는지 여부를 확인
     * @param email
     * @return
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    /**
     * 회원가입 요청을 처리하는 메서드
     *
     * <p>
     *     전달받은 사용자 정보를 기반으로 새로운 사용자를 생성하고 저장
     *     이메일이 이미 존재하는 경우 예외를 발생시킴
     *     성공적으로 저장된 사용자의 ID, 이메일, 권한을 포함하는 JWT 토큰을 반환
     * </p>
     *
     * @param userSignUpRequestsDto 회원가입 요청 DTO (이메일, 비밀번호, 사용자명 포함)
     * @return 생성된 사용자의 JWT 토큰
     * @throws CustomException 이메일이 이미 존재하는 경우
     */
    public String signup(UserSignUpRequestsDTO userSignUpRequestsDto) {
        String email = userSignUpRequestsDto.getEmail().toLowerCase(); // 소문자 통일
        
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_EXISTS);
        }

        User user = User.builder()
                .username(userSignUpRequestsDto.getUsername())
                .email(email) // 소문자로 변환된 email을 저장
                .password(passwordEncoder.encode(userSignUpRequestsDto.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        return jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * 보호자 회원가입 요청을 처리하는 메서드
     *
     * <p>
     *     전달받은 보호자 정보를 기반으로 새로운 보호자 사용자를 생성하고 저장
     *     이메일이 이미 존재하는 경우 예외를 발생시킴
     *     성공적으로 저장된 사용자의 ID, 이메일, 권한을 포함하는 JWT 토큰을 반환
     * </p>
     *
     * @param guardianSignUpRequestsDto 보호자 회원가입 요청 DTO (이메일, 비밀번호, 사용자명 포함)
     * @return 생성된 보호자 사용자의 JWT 토큰
     * @throws CustomException 이메일이 이미 존재하는 경우
     */
    public String guardianSignup(GuardianSignUpRequestsDTO guardianSignUpRequestsDto) {
        String email = guardianSignUpRequestsDto.getEmail().toLowerCase(); // 소문자 통일

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_EXISTS);
        }

        User user = User.builder()
                .username(guardianSignUpRequestsDto.getUsername())
                .email(email) // 소문자로 변환된 email을 저장
                .password(passwordEncoder.encode(guardianSignUpRequestsDto.getPassword()))
                .role(Role.GUARDIANS)
                .build();

        User savedUser = userRepository.save(user);
        return jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * 로그인 요청을 처리하는 메서드
     *
     * <p>
     *     전달받은 이메일과 비밀번호를 기반으로 사용자를 인증하고,
     *     유효한 경우 JWT AccessToken 과 사용자 ID를 반환
     *     이메일은 모두 소문자로 변환되어 조회됨
     * </p>
     *
     * @param userLoginRequestsDto 로그인 요청 DTO (이메일, 비밀번호 포함)
     * @return 인증에 성공한 사용자의 AccessToken과 사용자 ID를 담은 DTO
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     * @throws IllegalArgumentException 비밀번호가 틀린 경우
     */
    public Map<String, String> login(UserLoginRequestsDTO userLoginRequestsDto) {
        String email = userLoginRequestsDto.getEmail().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(userLoginRequestsDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 반환: accessToken + refreshToken
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
