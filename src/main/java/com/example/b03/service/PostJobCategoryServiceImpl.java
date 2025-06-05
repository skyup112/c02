package com.example.b03.service;

import com.example.b03.domain.JobCategory;
import com.example.b03.domain.Post;
import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import com.example.b03.dto.PostJobCategoryDTO;
import com.example.b03.repository.JobCategoryRepository;
import com.example.b03.repository.PostJobCategoryRepository;
import com.example.b03.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostJobCategoryServiceImpl implements PostJobCategoryService {

    private final PostJobCategoryRepository postJobCategoryRepository;
    private final PostRepository postRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public List<PostJobCategory> getJobCategoriesByPostId(Integer postId) {
        return postJobCategoryRepository.findByPost_PostId(postId);
    }

    @Override
    public List<PostJobCategory> getPostsByJobCategoryId(Integer jobCategoryId) {
        return postJobCategoryRepository.findByJobCategory_JobCategoryId(jobCategoryId);
    }

    @Override
    public List<JobCategory> getCategoriesByPostId(Integer postId) {
        return postJobCategoryRepository.findByPost_PostId(postId).stream()
                .map(PostJobCategory::getJobCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignJobCategoriesToPost(Integer postId, List<Integer> jobCategoryIds) {
        postJobCategoryRepository.deleteByPostId(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 존재하지 않습니다."));

        for (Integer categoryId : jobCategoryIds) {
            JobCategory category = jobCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("직무 카테고리 없음: " + categoryId));

            PostJobCategory postJobCategory = PostJobCategory.builder()
                    .post(post)
                    .jobCategory(category)
                    .build();

            postJobCategoryRepository.save(postJobCategory);
        }
    }

    @Override
    public List<PostJobCategoryDTO> getJobCategoryDTOsByPostId(Integer postId) {
        return postJobCategoryRepository.findByPost_PostId(postId).stream()
                .map(mapping -> modelMapper.map(mapping, PostJobCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByPostId(Integer postId) {
        postJobCategoryRepository.deleteByPostId(postId);
    }

    @Override
    public void deleteById(PostJobCategoryId id) {
        postJobCategoryRepository.deleteById(id);
    }
}
