package com.example.b03.security.service;

import com.example.b03.domain.Member;
import com.example.b03.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetch join을 사용하여 'MembershipType'까지 함께 조회
        Member member = memberRepository.findByLoginIdWithMembershipType(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음: " + username));

        if (member.getIsDeleted() != null && member.getIsDeleted()) {
            throw new UsernameNotFoundException("User is deactivated: " + username);
        }

        return new CustomUserDetails(member);
    }
}
