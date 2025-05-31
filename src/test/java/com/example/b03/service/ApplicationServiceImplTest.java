package com.example.b03.service;

import com.example.b03.domain.Member;
import com.example.b03.domain.MembershipType;
import com.example.b03.domain.Post;
import com.example.b03.dto.ApplicationDTO;
import com.example.b03.repository.ApplicationRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.PostRepository;
import com.example.b03.service.ApplicationService;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ApplicationServiceImplTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Integer memberNo;
    private Integer postId;

    @BeforeEach
    void setUp() {
        // ✅ 테스트용 회원 생성 (loginId는 매번 달라야 함)
        Member member = Member.builder()
                .loginId("testuser_" + System.currentTimeMillis())  // 중복 방지
                .password("pw1234")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("서울시 강남구")
                .phone("01012345678")
                .membershipType(MembershipType.builder().typeId((byte) 2).typeName("개인회원").build())
                .build();
        member = memberRepository.save(member);
        memberNo = member.getMemberNo();

        // ✅ 테스트용 공고 생성 (Company 없이 생성)
        Post post = Post.builder()
                .title("백엔드 개발자 모집")
                .description("Spring Boot 경험자 우대")
                .location("서울")
                .salary("면접 후 결정")
                .deadline(LocalDate.now().plusDays(30))
                .postedDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        post = postRepository.save(post);
        postId = post.getPostId();
    }

    @Test
    void testCreateApplication() {
        ApplicationDTO dto = ApplicationDTO.builder()
                .memberNo(memberNo)
                .postId(postId)
                .filePath("/uploads/test_resume.pdf")
                .build();

        ApplicationDTO saved = applicationService.create(dto);
        assertNotNull(saved.getApplicationId());
        System.out.println("✅ 지원서 등록 완료: " + saved);
    }

    @Test
    void testDuplicateApplicationCheck() {
        // 기존 지원 이력 제거 (중복 오류 방지)
        applicationRepository.findByPost_PostIdAndMember_MemberNo(postId, memberNo)
                .ifPresent(app -> applicationRepository.deleteById(app.getApplicationId()));

        ApplicationDTO dto = ApplicationDTO.builder()
                .memberNo(memberNo)
                .postId(postId)
                .filePath("/uploads/first.pdf")
                .build();
        applicationService.create(dto);

        boolean isDuplicate = applicationService.isDuplicateApplication(postId, memberNo);
        assertTrue(isDuplicate);
        System.out.println("✅ 중복 지원 여부 확인됨");
    }
}
