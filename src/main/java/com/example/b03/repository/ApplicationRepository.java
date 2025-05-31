package com.example.b03.repository;

import com.example.b03.domain.Application;
import com.example.b03.domain.Member;
import com.example.b03.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByPost_PostId(Integer postId);
    List<Application> findByPost(Post post);
    List<Application> findByMember_MemberNo(Integer memberNo);
    List<Application> findByMember(Member member);
    boolean existsByPostAndMember(Post post, Member member);

    // 기업회원의 공고 전체에 지원한 지원서 목록
    List<Application> findByPost_Company_Member(Member member);

    // 특정 공고와 기업회원 일치 여부 확인용
    @Query("SELECT a FROM Application a " +
            "JOIN a.post p " +
            "JOIN p.company c " +
            "WHERE c.member.memberNo = :companyMemberNo")
    List<Application> findApplicationsByCompanyMemberNo(@Param("companyMemberNo") Integer companyMemberNo);

    Optional<Application> findByPost_PostIdAndMember_MemberNo(Integer postId, Integer memberNo);
}
