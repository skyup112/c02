package com.example.b03.repository;

import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostJobCategoryRepository extends JpaRepository<PostJobCategory, PostJobCategoryId> {

    // 특정 공고에 연결된 직무 카테고리 매핑 목록
    List<PostJobCategory> findByPost_PostId(Integer postId);

    // 특정 직무 카테고리에 연결된 공고 목록
    List<PostJobCategory> findByJobCategory_JobCategoryId(Integer jobCategoryId);

    // 특정 공고의 매핑 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM PostJobCategory pjc WHERE pjc.post.postId = :postId")
    void deleteByPostId(@Param("postId") Integer postId);
}
