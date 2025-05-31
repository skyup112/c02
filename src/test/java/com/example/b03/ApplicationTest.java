package com.example.b03;

import com.example.b03.domain.*;
import com.example.b03.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class ApplicationTest {

    @Autowired private MembershipTypeRepository membershipTypeRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CompanyInfoRepository companyInfoRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private ApplicationRepository applicationRepository;

    @Test
    public void 개인회원_공고지원_테스트() {
        // 1. 기업회원 및 공고 등록
        MembershipType 기업회원 = new MembershipType((byte) 2, "기업회원");
        membershipTypeRepository.save(기업회원);

        Member 기업회원_계정 = new Member(null, "corp123", "pass", "기업명", LocalDate.of(1980, 1, 1), "서울", "02-1234-5678", 기업회원);
        memberRepository.save(기업회원_계정);

        CompanyInfo 회사정보 = new CompanyInfo();
        회사정보.setMember(기업회원_계정);
        회사정보.setCompanyName("테스트회사");
        회사정보.setFoundedDate(LocalDate.of(2000, 1, 1));
        회사정보.setEmployeeCount(100);
        회사정보.setRevenue(1_000_000_000L);
        회사정보.setTechStack("Java, Spring");
        회사정보.setLocation("서울");
        회사정보.setHomepageUrl("https://test.com");
        회사정보.setDescription("테스트 설명");
        companyInfoRepository.save(회사정보);

        Post 공고 = new Post();
        공고.setCompany(회사정보);
        공고.setTitle("백엔드 개발자 모집");
        공고.setDescription("백엔드 개발자 채용 공고입니다.");
        공고.setSalary("5000만");
        공고.setLocation("서울");
        공고.setPostedDate(LocalDateTime.now());
        공고.setUpdatedDate(LocalDateTime.now());
        공고.setDeadline(LocalDate.now().plusDays(30));
        postRepository.save(공고);

        // 2. 개인회원 등록
        MembershipType 개인회원 = new MembershipType((byte) 3, "개인회원");
        membershipTypeRepository.save(개인회원);

        Member 개인회원_계정 = new Member(null, "user123", "password", "홍길동", LocalDate.of(1995, 5, 10), "부산", "010-5678-1234", 개인회원);
        memberRepository.save(개인회원_계정);

        // 3. 공고 지원
        Application 지원서 = new Application();
        지원서.setPost(공고);
        지원서.setMember(개인회원_계정);
        지원서.setFilePath("/resumes/user123_resume.pdf");
        지원서.setSubmittedAt(LocalDateTime.now());
        지원서.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(지원서);

        // 4. 검증
        assertThat(applicationRepository.findAll()).hasSize(1);
        assertThat(applicationRepository.findByPost_PostId(공고.getPostId())).hasSize(1);
    }
}

