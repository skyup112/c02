package com.example.b03.service;

import com.example.b03.domain.JobCategory;
import com.example.b03.domain.Post;
import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import com.example.b03.repository.JobCategoryRepository;
import com.example.b03.repository.PostJobCategoryRepository;
import com.example.b03.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostJobCategoryServiceImpl implements PostJobCategoryService {

    private final PostJobCategoryRepository postJobCategoryRepository;
    private final PostRepository postRepository;
    private final JobCategoryRepository jobCategoryRepository;

    // 단일 카테고리 추가
    @Override
    public void addJobCategoryToPost(Integer postId, Integer jobCategoryId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));
        JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("직무 카테고리가 존재하지 않습니다."));

        PostJobCategory mapping = PostJobCategory.builder()
                .post(post)
                .jobCategory(jobCategory)
                .build();

        postJobCategoryRepository.save(mapping);
    }

    // 특정 공고에 등록된 모든 카테고리 조회
    @Override
    public List<PostJobCategory> getJobCategoriesByPostId(Integer postId) {
        return postJobCategoryRepository.findByPost_PostId(postId);
    }

    // 특정 카테고리에 속한 공고 조회
    @Override
    public List<PostJobCategory> getPostsByJobCategoryId(Integer jobCategoryId) {
        return postJobCategoryRepository.findByJobCategory_JobCategoryId(jobCategoryId);
    }

    // 여러 카테고리 일괄 추가
    @Override
    @Transactional
    public void assignJobCategoriesToPost(Integer postId, List<Integer> jobCategoryIds) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        postJobCategoryRepository.deleteByPost_PostId(postId); // 기존 삭제

        List<PostJobCategory> mappings = jobCategoryIds.stream()
                .map(jobCategoryId -> {
                    JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId)
                            .orElseThrow(() -> new IllegalArgumentException("직무 카테고리가 존재하지 않습니다."));
                    return PostJobCategory.builder()
                            .post(post)
                            .jobCategory(jobCategory)
                            .build();
                })
                .collect(Collectors.toList());

        postJobCategoryRepository.saveAll(mappings);
    }

    // 간단히 위 getJobCategoriesByPostId와 중복일 수 있음
    @Override
    public List<PostJobCategory> getJobCategoriesByPost(Integer postId) {
        return postJobCategoryRepository.findByPost_PostId(postId);
    }

    // 특정 공고의 모든 직무카테고리 연결 제거
    @Override
    public void removeAllJobCategoriesFromPost(Integer postId) {
        postJobCategoryRepository.deleteByPost_PostId(postId);
    }

    // 복합키로 단일 삭제
    @Override
    public void deleteById(PostJobCategoryId id) {
        postJobCategoryRepository.deleteById(id);
    }
}
