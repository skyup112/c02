package com.example.b03.service;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Member;
import com.example.b03.domain.MembershipType;
import com.example.b03.dto.PostDTO;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.b03.B03Application.class) // 메인 클래스 지정
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class PostServiceImplTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    private Integer companyMemberNo;

    @BeforeEach
    void setUp() {
        // 중복 방지용 고유 loginId 생성
        String uniqueLoginId = "company_" + System.currentTimeMillis();

        // 기업 회원 생성
        Member member = Member.builder()
                .loginId(uniqueLoginId)
                .password("pw1234")
                .name("기업테스트")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("서울")
                .phone("01099990000")
                .membershipType(MembershipType.builder().typeId((byte) 3).typeName("기업회원").build())
                .build();
        member = memberRepository.save(member);
        companyMemberNo = member.getMemberNo();

        // CompanyInfo 생성 (Post에 필요)
        CompanyInfo companyInfo = CompanyInfo.builder()
                .member(member)
                .companyName("테스트기업")
                .foundedDate(LocalDate.of(2010, 1, 1))
                .employeeCount(50)
                .revenue(10_000_000L)
                .techStack("Spring,Java")
                .address("서울 강남")
                .homepageUrl("https://test.com")
                .description("테스트 기업 소개입니다.")
                .build();
        companyInfoRepository.save(companyInfo);
    }

    @Test
    void testCreatePost() {
        PostDTO dto = PostDTO.builder()
                .memberNo(companyMemberNo)
                .title("백엔드 개발자 모집")
                .description("Spring Boot 가능한 개발자 모집")
                .salary("면접 후 결정")
                .address("서울시 강남구")
                .deadline(LocalDate.now().plusDays(30))
                .build();

        PostDTO saved = postService.createPost(dto);
        assertNotNull(saved.getPostId());
        System.out.println("✅ 공고 등록 완료: " + saved);
    }

    @Test
    void testGetPostsByCompany() {
        // 공고 등록
        testCreatePost();

        // 공고 목록 조회
        List<PostDTO> posts = postService.getPostsByCompany(companyMemberNo);
        assertFalse(posts.isEmpty());
        System.out.println("✅ 기업별 공고 목록 조회: " + posts);
    }
}
