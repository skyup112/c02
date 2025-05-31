//package com.example.b03;
//
//import com.example.b03.domain.Member;
//import com.example.b03.domain.MembershipType;
//import com.example.b03.repository.MemberRepository;
//import com.example.b03.repository.MembershipTypeRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//public class RepositoryTest {
//
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private MembershipTypeRepository membershipTypeRepository;
//
//    private MembershipType 개인회원;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 MembershipType 저장
//        개인회원 = new MembershipType((byte) 3, "개인회원");
//        membershipTypeRepository.save(개인회원);
//    }
//
//    @Test
//    void 회원_저장_및_조회_테스트() {
//        // given
//        Member member = Member.builder()
//                .loginId("testuser")
//                .password("password123")
//                .name("홍길동")
//                .birthDate(LocalDate.of(1990, 1, 1))
//                .address("서울특별시")
//                .phone("010-1234-5678")
//                .membershipType(개인회원)
//                .build();
//
//        // when
//        Member saved = memberRepository.save(member);
//
//        // then
//        Member found = memberRepository.findById(saved.getMemberNo()).orElse(null);
//        assertThat(found).isNotNull();
//        assertThat(found.getLoginId()).isEqualTo("testuser");
//        assertThat(found.getMembershipType().getTypeName()).isEqualTo("개인회원");
//    }
//}
