// 8. Application
package com.example.b03.repository;

import com.example.b03.domain.Application;
import com.example.b03.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByPost_PostId(Integer postId);
    List<Application> findByPost(Post post);

    // 기업 회원이 등록한 공고들에 대한 지원서만 가져오기
    List<Application> findByPost_Company_Member_MemberNo(Integer memberNo);
}