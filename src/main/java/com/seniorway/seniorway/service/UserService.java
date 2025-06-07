package com.seniorway.seniorway.service;

import com.seniorway.seniorway.config.JwtTokenProvider;
import com.seniorway.seniorway.dto.UserLoginRequestsDto;
import com.seniorway.seniorway.dto.UserLoginResponseDTO;
import com.seniorway.seniorway.dto.UserSignUpRequestsDto;
import com.seniorway.seniorway.entity.User;
import com.seniorway.seniorway.repository.UserRepository;
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
     * 회원가입 요청 처리
     * 주어진 signUp DTO를 바탕으로 이메일 중복 여부 체크
     * 비밀번호를 암호화 하여 User Entity를 생성 후 DB 에 저장
     * @param userSignUpRequestsDto 회원가입용 User 정보
     * @return 저장된 User Entity
     * @throws IllegalArgumentException 이메일이 존재할 경우 예외 발생
     */
    public String signUp(UserSignUpRequestsDto userSignUpRequestsDto) {
        if (userRepository.existsByEmail(String.valueOf(userSignUpRequestsDto.getEmail()))) {
            throw new IllegalArgumentException("Email address already in use");
        }

        // TODO: 왜 String 으로 변환 해야 하는지 check!
        User user = User.builder()
                .username(userSignUpRequestsDto.getUsername())
                .email(String.valueOf(userSignUpRequestsDto.getEmail()))
                .password(passwordEncoder.encode(userSignUpRequestsDto.getPassword()))
                .role("ROLE_USER")
                .build();

        User saveUser = userRepository.save(user);
        return jwtTokenProvider.createToken(saveUser.getId(), saveUser.getEmail(), saveUser.getRole());
    }

    public UserLoginResponseDTO login(UserLoginRequestsDto userLoginRequestsDto) {
        User user = userRepository.findByEmail(userLoginRequestsDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(userLoginRequestsDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀전호가 틀립니다");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        return new UserLoginResponseDTO(accessToken, user.getId());
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
