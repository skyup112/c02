// 8. 지원서
package com.example.b03.repository;

import com.example.b03.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    // 추가로 필요한 커스텀 메서드 예시:

    // 특정 게시글(post)에 대한 모든 지원자 조회
    List<Application> findByPost_PostId(Integer postId);

    // 특정 회원(member)의 지원 이력 조회
    List<Application> findByMember_MemberNo(Integer memberNo);

    // 삭제되지 않은 지원서만 조회
    List<Application> findByIsDeletedFalse();
}
