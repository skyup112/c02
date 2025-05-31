package com.example.b03;

import com.example.b03.domain.*;
import com.example.b03.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
public class BoardAndInquiryEntityTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private MembershipTypeRepository membershipTypeRepository;
    @Autowired private BoardPostRepository boardPostRepository;
    @Autowired private BoardCommentRepository boardCommentRepository;
    @Autowired private InquiryRepository inquiryRepository;
    @Autowired private InquiryCommentRepository inquiryCommentRepository;

    private Member user;
    private Member admin;

    @BeforeEach
    void setUp() {
        MembershipType userType = membershipTypeRepository.save(
                MembershipType.builder()
                        .typeId((byte) 3) // 개인회원
                        .typeName("개인회원")
                        .build()
        );

        MembershipType adminType = membershipTypeRepository.save(
                MembershipType.builder()
                        .typeId((byte) 1) // 관리자
                        .typeName("관리자")
                        .build()
        );

        user = Member.builder()
                .loginId("user" + UUID.randomUUID())
                .password("1234")
                .name("홍길동")
                .address("서울시 강남구")
                .phone("010-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .membershipType(userType)
                .build();

        admin = Member.builder()
                .loginId("admin" + UUID.randomUUID())
                .password("admin1234")
                .name("관리자")
                .address("서울시 중구")
                .phone("010-0000-0000")
                .birthDate(LocalDate.of(1985, 5, 10))
                .membershipType(adminType)
                .build();

        memberRepository.save(user);
        memberRepository.save(admin);
    }

    @Test
    @Order(1)
    void 게시글_작성_및_삭제_검증() {
        // 게시글 생성
        BoardPost post = BoardPost.builder()
                .member(user)
                .title("첫 게시글")
                .content("내용입니다.")
                .build();

        boardPostRepository.save(post);

        // 삭제되지 않은 상태 확인
        assertThat(post.getIsDeleted()).isFalse();

        // 삭제 처리 (논리적)
        post.setIsDeleted(true);
        boardPostRepository.save(post);

        // DB에서 다시 조회하여 확인 (정확한 검증)
        Optional<BoardPost> deletedPost = boardPostRepository.findById(post.getPostId());

        assertThat(deletedPost).isPresent();
        assertThat(deletedPost.get().getIsDeleted()).isTrue();
    }

    @Test
    @Order(2)
    void 게시글에_댓글_작성() {
        BoardPost post = boardPostRepository.save(BoardPost.builder()
                .member(user)
                .title("댓글 게시글")
                .content("댓글을 달아보세요")
                .build());

        BoardComment comment = BoardComment.builder()
                .boardPost(post)
                .member(user)
                .content("좋은 글이네요!")
                .build();

        boardCommentRepository.save(comment);

        assertThat(comment.getIsDeleted()).isFalse();
    }

    @Test
    @Order(3)
    void 문의글_작성_및_삭제() {
        Inquiry inquiry = inquiryRepository.save(
                Inquiry.builder()
                        .member(user)
                        .title("로그인 오류")
                        .content("비밀번호가 틀린 것 같아요.")
                        .build()
        );

        assertThat(inquiry.getIsDeleted()).isFalse();

        inquiry.setIsDeleted(true);
        inquiryRepository.save(inquiry);

        assertThat(inquiry.getIsDeleted()).isTrue();
    }

    @Test
    @Order(4)
    void 문의글에_관리자_답변_작성() {
        Inquiry inquiry = inquiryRepository.save(
                Inquiry.builder()
                        .member(user)
                        .title("기능 문의")
                        .content("지원서가 안 올라가요.")
                        .build()
        );

        InquiryComment comment = InquiryComment.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content("파일 확장자를 확인해 주세요.")
                .build();

        inquiryCommentRepository.save(comment);

        assertThat(comment.getIsDeleted()).isFalse();
    }
}
