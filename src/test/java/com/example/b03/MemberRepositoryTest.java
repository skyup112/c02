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
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.annotation.Rollback;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//
//@SpringBootTest
//@Transactional
//@Rollback(false)
//public class MemberRepositoryTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MembershipTypeRepository membershipTypeRepository;
//
//    private MembershipType basicMembership;
//
//    @BeforeEach
//    void setUp() {
//        // 기본 회원권 등록
//        basicMembership = MembershipType.builder()
//                .typeId((byte) 1)
//                .typeName("BASIC")
//                .build();
//        membershipTypeRepository.save(basicMembership);
//    }
//
//    @Test
//    void 회원가입_성공_테스트() {
//        // given
//        Member member = Member.builder()
//                .loginId("testuser01")
//                .password("securePassword!")
//                .name("테스트유저")
//                .birthDate(LocalDate.of(1995, 5, 20))
//                .address("서울시 강남구")
//                .phone("010-1234-5678")
//                .membershipType(basicMembership)
//                .build();
//
//        // when
//        Member savedMember = memberRepository.save(member);
//        Member foundMember = memberRepository.findByLoginId("testuser01").orElse(null);
//
//        // then
//        assertThat(foundMember).isNotNull();
//        assertThat(foundMember.getLoginId()).isEqualTo("testuser01");
//        assertThat(foundMember.getName()).isEqualTo("테스트유저");
//        assertThat(foundMember.getMembershipType().getTypeName()).isEqualTo("BASIC");
//    }
//    @Test
//    void 회원가입_실패_중복ID_테스트() {
//        // given
//        Member member1 = Member.builder()
//                .loginId("duplicateUser")
//                .password("pass1")
//                .name("사용자1")
//                .birthDate(LocalDate.of(1990, 1, 1))
//                .address("서울")
//                .phone("010-1111-2222")
//                .membershipType(basicMembership)
//                .build();
//
//        Member member2 = Member.builder()
//                .loginId("duplicateUser")  // 동일한 loginId
//                .password("pass2")
//                .name("사용자2")
//                .birthDate(LocalDate.of(1992, 2, 2))
//                .address("부산")
//                .phone("010-3333-4444")
//                .membershipType(basicMembership)
//                .build();
//
//        // when
//        memberRepository.save(member1);
//
//        // then: 중복 loginId 저장 시 예외 발생
//        assertThatThrownBy(() -> memberRepository.saveAndFlush(member2))
//                .isInstanceOf(DataIntegrityViolationException.class);
//    }
//}
