package com.seniorway.seniorway.service;

import com.seniorway.seniorway.entity.User;
import com.seniorway.seniorway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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


}
