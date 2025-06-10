package com.example.b03.security.service;

import com.example.b03.domain.Member;
import com.example.b03.dto.MemberDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class CustomUserDetails extends User {
    private final Member member;

    public CustomUserDetails(Member member) {
        super(member.getLoginId(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }

    // Member 객체 반환
    public Member getMember() {
        return this.member;
    }

    // Member -> MemberDTO 변환
    public MemberDTO getMemberDTO() {
        return MemberDTO.fromEntity(this.member);
    }

    public Byte getMembershipTypeId() {
        return member.getMembershipType().getTypeId();
    }
}
