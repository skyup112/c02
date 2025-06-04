package com.example.b03.service;

import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import com.example.b03.dto.PostJobCategoryDTO;

import java.util.List;

public interface PostJobCategoryService {

    // 공고에 직무 카테고리 추가
    void addJobCategoryToPost(Integer postId, Integer jobCategoryId);

    // 공고에 연결된 직무 카테고리 목록 조회
    List<PostJobCategory> getJobCategoriesByPostId(Integer postId);

    // 특정 직무 카테고리에 속한 공고 목록 조회
    List<PostJobCategory> getPostsByJobCategoryId(Integer jobCategoryId);

    // 특정 공고에 연결된 직무카테고리 목록 조회
    List<PostJobCategory> getJobCategoriesByPost(Integer postId);

    // 특정 공고의 직무카테고리 연결 삭제
    void removeAllJobCategoriesFromPost(Integer postId);

    // 복합키로 특정 항목 삭제
    void deleteById(PostJobCategoryId id);

    void assignJobCategoriesToPost(Integer postId, List<Integer> jobCategoryIds); // ✅ 이 메서드 반드시 있어야 함

    List<PostJobCategoryDTO> getJobCategoryDTOsByPostId(Integer postId);
}
