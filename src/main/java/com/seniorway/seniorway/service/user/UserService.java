package com.seniorway.seniorway.service.user;

import com.seniorway.seniorway.config.jwt.JwtTokenProvider;
import com.seniorway.seniorway.dto.user.UserLoginRequestsDto;
import com.seniorway.seniorway.dto.user.UserLoginResponseDTO;
import com.seniorway.seniorway.dto.user.UserSignUpRequestsDto;
import com.seniorway.seniorway.entity.User;
import com.seniorway.seniorway.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
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
     * 회원가입 요청 처리
     * 주어진 signUp DTO를 바탕으로 이메일 중복 여부 체크
     * 비밀번호를 암호화 하여 User Entity를 생성 후 DB 에 저장
     * @param userSignUpRequestsDto 회원가입용 User 정보
     * @return 저장된 User Entity
     * @throws IllegalArgumentException 이메일이 존재할 경우 예외 발생
     */
    public String signUp(UserSignUpRequestsDto userSignUpRequestsDto) {
        String email = userSignUpRequestsDto.getEmail().toLowerCase(); // 소문자 통일
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email address already in use");
        }

        User user = User.builder()
                .username(userSignUpRequestsDto.getUsername())
                .email(email) // 소문자로 변환된 email을 저장
                .password(passwordEncoder.encode(userSignUpRequestsDto.getPassword()))
                .role("USER")
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
    public UserLoginResponseDTO login(UserLoginRequestsDto userLoginRequestsDto) {
        String email = userLoginRequestsDto.getEmail().toLowerCase(); // 소문자 통일

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(userLoginRequestsDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀립니다");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        return new UserLoginResponseDTO(accessToken, user.getId());
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
