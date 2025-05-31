package com.example.b03.repository;

import com.example.b03.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByCompany_Member_MemberNo(Integer memberNo); // 기업회원이 등록한 공고 목록
}
