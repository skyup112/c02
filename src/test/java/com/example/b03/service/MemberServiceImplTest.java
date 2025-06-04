package com.example.b03.service;

import com.example.b03.dto.MemberDTO;
import com.example.b03.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MariaDB 사용
@Transactional
@Commit
public class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void createAdminMember() {
        MemberDTO dto = MemberDTO.builder()
                .loginId("admin001")
                .password("adminpass")
                .name("관리자")
                .birthDate(LocalDate.of(1980, 1, 1))
                .address("서울시 본사")
                .phone("01011112222")
                .membershipTypeId((byte) 1) // 관리자
                .build();

        MemberDTO saved = memberService.createMember(dto);
        assertNotNull(saved.getMemberNo());
        System.out.println("✅ 관리자 회원 등록 성공: " + saved);
    }

    @Test
    void createPersonalMember() {
        MemberDTO dto = MemberDTO.builder()
                .loginId("user001")
                .password("userpass")
                .name("홍길동")
                .birthDate(LocalDate.of(1995, 5, 5))
                .address("서울시 성북구")
                .phone("01022223333")
                .membershipTypeId((byte) 3) // 개인회원
                .build();

        MemberDTO saved = memberService.createMember(dto);
        assertNotNull(saved.getMemberNo());
        System.out.println("✅ 개인회원 등록 성공: " + saved);
    }

    @Test
    void createCompanyMember() {
        MemberDTO dto = MemberDTO.builder()
                .loginId("company001")
                .password("comppass")
                .name("기업 대표")
                .birthDate(LocalDate.of(1988, 3, 15))
                .address("서울시 강남구")
                .phone("01033334444")
                .membershipTypeId((byte) 2) // 기업회원
                .build();

        MemberDTO saved = memberService.createMember(dto);
        assertNotNull(saved.getMemberNo());
        System.out.println("✅ 기업회원 등록 성공: " + saved);
    }
}
