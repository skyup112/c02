//package com.example.b03;
//
//import com.example.b03.domain.*;
//import com.example.b03.repository.*;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//@Rollback(false)
//public class ApplicationViewTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MembershipTypeRepository membershipTypeRepository;
//
//    @Autowired
//    private CompanyInfoRepository companyInfoRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private ApplicationRepository applicationRepository;
//
//    @Test
//    @DisplayName("기업회원은 자신의 공고에 지원된 이력서만 열람할 수 있다")
//    void 기업회원은_자신의_공고에_지원된_이력서만_열람할_수_있다() {
//        // given
//        MembershipType 기업회원타입 = membershipTypeRepository.save(MembershipType.builder()
//                .typeId((byte) 2)
//                .typeName("기업회원")
//                .build());
//
//        Member 기업A = memberRepository.save(Member.builder()
//                .loginId("companyA")
//                .password("pass")
//                .name("기업 A")
//                .address("서울")
//                .phone("010-1234-5678")
//                .membershipType(기업회원타입)
//                .build());
//
//        Member 기업B = memberRepository.save(Member.builder()
//                .loginId("companyB")
//                .password("pass")
//                .name("기업 B")
//                .address("부산")
//                .phone("010-0000-0000")
//                .membershipType(기업회원타입)
//                .build());
//
//        CompanyInfo 기업A_Info = companyInfoRepository.save(CompanyInfo.builder()
//                .member(기업A)
//                .companyName("A주식회사")
//                .location("서울")
//                .build());
//
//        CompanyInfo 기업B_Info = companyInfoRepository.save(CompanyInfo.builder()
//                .member(기업B)
//                .companyName("B주식회사")
//                .location("부산")
//                .build());
//
//        Post 기업A공고 = postRepository.save(Post.builder()
//                .company(기업A_Info)
//                .title("백엔드 개발자 모집")
//                .description("우리는 스타트업입니다.")
//                .salary("협의")
//                .location("서울")
//                .deadline(LocalDate.now().plusWeeks(2))
//                .postedDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .build());
//
//        // 개인회원 생성 및 지원
//        MembershipType 개인회원타입 = membershipTypeRepository.save(MembershipType.builder()
//                .typeId((byte) 3)
//                .typeName("개인회원")
//                .build());
//
//        Member 개인회원 = memberRepository.save(Member.builder()
//                .loginId("user12312312")
//                .password("1234")
//                .name("홍길동")
//                .address("경기")
//                .phone("010-1111-2222")
//                .birthDate(LocalDate.of(1990, 1, 1))
//                .membershipType(개인회원타입)
//                .build());
//
//        Application 지원서 = applicationRepository.save(Application.builder()
//                .post(기업A공고)
//                .member(개인회원)
//                .filePath("/resumes/hong_resume.pdf")
//                .submittedAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build());
//
//        // when
//        List<Application> 기업A_조회 = applicationRepository.findByPost_Company_Member_MemberNo(기업A.getMemberNo());
//        List<Application> 기업B_조회 = applicationRepository.findByPost_Company_Member_MemberNo(기업B.getMemberNo());
//
//        // then
//        assertThat(기업A_조회).hasSize(1);
//        assertThat(기업A_조회.get(0).getMember().getLoginId()).isEqualTo("user12312312");
//
//        assertThat(기업B_조회).isEmpty(); // 다른 기업은 열람 불가
//    }
//}
