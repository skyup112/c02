package com.example.b03.repository;

import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostJobCategoryRepository extends JpaRepository<PostJobCategory, PostJobCategoryId> {
    // 특정 공고에 연결된 직무 카테고리 목록 조회
    List<PostJobCategory> findByPost_PostId(Integer postId);

    // 특정 직무 카테고리에 속한 공고 목록 조회
    List<PostJobCategory> findByJobCategory_JobCategoryId(Integer jobCategoryId);

    void deleteByPost_PostId(Integer postId);
}
