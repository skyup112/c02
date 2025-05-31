package com.example.b03;

import com.example.b03.domain.*;
import com.example.b03.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class CompanyMemberRegistrationTest {

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private CompanyJobCategoryRepository companyJobCategoryRepository;

    @Test
    public void 기업회원등록_및_연관엔티티_저장_테스트() {
        // 1. MembershipType 저장 (기업회원: type_id = 2)
        MembershipType companyType = new MembershipType((byte) 2, "기업회원");
        membershipTypeRepository.save(companyType);

        // 2. Member 저장
        Member member = new Member();
        member.setLoginId("company_user_001");
        member.setPassword("securePassword123");
        member.setName("홍길동");
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setAddress("서울시 강남구");
        member.setPhone("010-1234-5678");
        member.setMembershipType(companyType);
        Member savedMember = memberRepository.save(member);

        // 3. CompanyInfo 저장
        CompanyInfo company = new CompanyInfo();
        company.setMember(savedMember);
        company.setCompanyName("오픈AI 코리아");
        company.setFoundedDate(LocalDate.of(2020, 5, 20));
        company.setEmployeeCount(50);
        company.setRevenue(5_000_000_000L);
        company.setTechStack("Java, Spring Boot, AWS");
        company.setLocation("서울특별시");
        company.setHomepageUrl("https://www.openai.com");
        company.setDescription("AI 기술을 연구하는 회사입니다.");
        companyInfoRepository.save(company);

        // 4. JobCategory 저장 및 연결
        JobCategory backend = jobCategoryRepository.save(new JobCategory(null, "백엔드 개발자"));
        JobCategory frontend = jobCategoryRepository.save(new JobCategory(null, "프론트엔드 개발자"));

        CompanyJobCategory companyBackend = new CompanyJobCategory();
        companyBackend.setCompany(company);
        companyBackend.setMemberNo(company.getMemberNo());
        companyBackend.setJobCategory(backend);
        companyBackend.setJobCategoryId(backend.getJobCategoryId());

        CompanyJobCategory companyFrontend = new CompanyJobCategory();
        companyFrontend.setCompany(company);
        companyFrontend.setMemberNo(company.getMemberNo());
        companyFrontend.setJobCategory(frontend);
        companyFrontend.setJobCategoryId(frontend.getJobCategoryId());

        companyJobCategoryRepository.saveAll(List.of(companyBackend, companyFrontend));

        // ✅ 검증
        assertThat(memberRepository.findById(savedMember.getMemberNo())).isPresent();
        assertThat(companyInfoRepository.findById(company.getMemberNo())).isPresent();
        assertThat(companyJobCategoryRepository.findAllByCompany_MemberNo(company.getMemberNo())).hasSize(2);

    }
}
