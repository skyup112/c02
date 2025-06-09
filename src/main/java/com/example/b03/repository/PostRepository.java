package com.example.b03.repository;

import com.example.b03.domain.Post;
import com.example.b03.repository.search.PostSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.zip.ZipFile;

public interface PostRepository extends JpaRepository<Post, Integer>, PostSearch {
    List<Post> findByCompany_Member_MemberNo(Integer memberNo); // 기업회원이 등록한 공고 목록
    List<Post> findAllByOrderByPostedDateDesc();
    // ✅ 직무 카테고리 기반 필터링
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN PostJobCategory pc ON p.postId = pc.post.postId " +
            "WHERE pc.jobCategory.jobCategoryId = :categoryId")
    List<Post> findByJobCategoryId(@Param("categoryId") Integer categoryId);

    List<Post> findByCompany_Member_IsDeletedFalse();
}

