package com.example.b03;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Member;
import com.example.b03.domain.MembershipType;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.MembershipTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompanyInfoRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Test
    @Transactional
    public void insertCompanyInfoTest() {
        // 1. 먼저 MembershipType 저장
        MembershipType membershipType = new MembershipType();
        membershipType.setTypeId((byte) 2); // 예: 기업회원
        membershipType.setTypeName("기업회원");
        MembershipTypeRepository.save(membershipType);

        // 2. Member 생성 및 저장
        Member member = new Member();
        member.setLoginId("companyUser01");
        member.setPassword("securePassword123");
        member.setName("오픈AI 기업");
        member.setBirthDate(LocalDate.of(2020, 1, 15));
        member.setAddress("서울 강남구 테헤란로 123");
        member.setPhone("02-1234-5678");
        member.setMembershipType(membershipType);

        member = memberRepository.save(member); // 저장 후 ID 확인용

        // 3. CompanyInfo 저장
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setMember(member);  // FK 연관
        companyInfo.setCompanyName("오픈AI 코리아");
        companyInfo.setFoundedDate(LocalDate.of(2020, 1, 15));
        companyInfo.setEmployeeCount(100);
        companyInfo.setRevenue(1_000_000_000L);
        companyInfo.setTechStack("Java, Spring Boot, AI, GPT");
        companyInfo.setLocation("서울 강남구");
        companyInfo.setHomepageUrl("https://openai.com");
        companyInfo.setDescription("AI 기반 솔루션을 개발하는 회사");

        companyInfoRepository.save(companyInfo);

        // 4. 검증
        CompanyInfo saved = companyInfoRepository.findById(member.getMemberNo()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getCompanyName()).isEqualTo("오픈AI 코리아");
    }
}
