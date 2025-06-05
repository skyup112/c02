package com.example.b03.service;

import com.example.b03.domain.JobCategory;
import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import com.example.b03.dto.PostJobCategoryDTO;

import java.util.List;

public interface PostJobCategoryService {

    // 단일 카테고리 추가
    void addJobCategoryToPost(Integer postId, Integer jobCategoryId);

    // 특정 공고에 연결된 모든 직무 카테고리 매핑 반환
    List<PostJobCategory> getJobCategoriesByPostId(Integer postId);

    // 특정 직무 카테고리에 연결된 공고 매핑 반환
    List<PostJobCategory> getPostsByJobCategoryId(Integer jobCategoryId);

    // 불필요한 중복 메서드 제거됨 → getCategoriesByPostId로 명확화
    List<JobCategory> getCategoriesByPostId(Integer postId); // ✅ 새로운 핵심 메서드

    // 공고에 직무 카테고리 일괄 매핑
    void assignJobCategoriesToPost(Integer postId, List<Integer> jobCategoryIds);

    // DTO 반환용
    List<PostJobCategoryDTO> getJobCategoryDTOsByPostId(Integer postId);

    // 특정 공고의 매핑 삭제
    void deleteByPostId(Integer postId);

    // 복합키로 삭제
    void deleteById(PostJobCategoryId id);
}
