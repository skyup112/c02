package com.example.b03;

import com.example.b03.domain.*;
import com.example.b03.domain.CompanyJobCategoryId;
import com.example.b03.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static com.example.b03.domain.QMember.member;
import static com.example.b03.domain.QMembershipType.membershipType;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @BeforeEach
    public void setup() {
        MembershipType admin = new MembershipType( (byte)1,"관리자");
        MembershipType personal = new MembershipType((byte) 2, "개인회원");
        MembershipType company = new MembershipType((byte) 3, "기업회원");
        membershipTypeRepository.save(admin);
        membershipTypeRepository.save(personal);
        membershipTypeRepository.save(company);
    }

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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostJobCategoryRepository postJobCategoryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BoardPostRepository boardPostRepository;

    @Autowired
    private BoardCommentRepository boardCommentRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryCommentRepository inquiryCommentRepository;

    @Test
    public void testMembershipTypeInsert() {
        // 1. 먼저 MembershipType 저장
        MembershipType membershipType = new MembershipType();
        membershipType.setTypeId((byte) 2); // 예: 기업회원
        membershipType.setTypeName("기업회원");
        membershipTypeRepository.save(membershipType);

    }

    @Test
    public void testMemberInsert() {
        MembershipType type = new MembershipType((byte)2,"개인회원");
        membershipTypeRepository.save(type); // 먼저 저장

        Member member = new Member();
        member.setLoginId("companyUser01");
        member.setPassword("securePassword123");
        member.setName("오픈AI 기업");
        member.setBirthDate(LocalDate.of(2020, 1, 15));
        member.setAddress("서울 강남구 테헤란로 123");
        member.setPhone("02-1234-5678");
        member.setMembershipType(membershipType);
    }

    @Test
    public void testCompanyInfoInsert() {
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

    }

//    @Test
//    public void testJobCategoryInsert() {
//        JobCategory job = new JobCategory();
//        job.setName("QA 엔지니어");
//        jobCategoryRepository.save(job);
//    }

//    @Test
//    public void testCompanyJobCategoryInsert() {
//        CompanyJobCategoryId id = new CompanyJobCategoryId(3, 1);
//        CompanyJobCategory cjc = new CompanyJobCategory();
//        cjc.setId(id);
//        companyJobCategoryRepository.save(cjc);
//    }
//
//    @Test
//    public void testPostInsert() {
//        Post post = new Post();
//        post.setMemberNo(3);
//        post.setTitle("QA 엔지니어 모집");
//        post.setDescription("자동화 테스트 엔지니어");
//        post.setSalary("면접 후 결정");
//        post.setLocation("서울");
//        post.setDeadline(LocalDate.now().plusDays(30));
//        postRepository.save(post);
//    }
//
//    @Test
//    public void testPostJobCategoryInsert() {
//        PostJobCategory pjc = new PostJobCategory();
//        pjc.setPostId(1);
//        pjc.setJobCategoryId(1);
//        postJobCategoryRepository.save(pjc);
//    }
//
//    @Test
//    public void testApplicationInsert() {
//        Application app = new Application();
//        app.setPostId(1);
//        app.setMemberNo(2);
//        app.setFilePath("/uploads/test.pdf");
//        applicationRepository.save(app);
//    }
//
//    @Test
//    public void testBoardPostInsert() {
//        BoardPost post = new BoardPost();
//        post.setMemberNo(2);
//        post.setTitle("자유게시판 글");
//        post.setContent("내용입니다.");
//        boardPostRepository.save(post);
//    }
//
//    @Test
//    public void testBoardCommentInsert() {
//        BoardComment comment = new BoardComment();
//        comment.setPostId(1);
//        comment.setMemberNo(2);
//        comment.setContent("댓글입니다.");
//        boardCommentRepository.save(comment);
//    }
//
//    @Test
//    public void testInquiryInsert() {
//        Inquiry inquiry = new Inquiry();
//        inquiry.setMemberNo(2);
//        inquiry.setTitle("문의드립니다.");
//        inquiry.setContent("1:1 문의 테스트");
//        inquiryRepository.save(inquiry);
//    }
//
//    @Test
//    public void testInquiryCommentInsert() {
//        InquiryComment comment = new InquiryComment();
//        comment.setInquiryId(1);
//        comment.setAdminNo(1);
//        comment.setContent("답변입니다.");
//        inquiryCommentRepository.save(comment);
//    }
}
