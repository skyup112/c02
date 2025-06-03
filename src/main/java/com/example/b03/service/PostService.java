package com.example.b03.service;

import com.example.b03.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO dto);
    PostDTO updatePost(PostDTO dto);
    void deletePost(Integer postId);
    PostDTO getPost(Integer postId);
    List<PostDTO> getPostsByCompany(Integer memberNo); // 기업 회원 전용
    // 모든 공고 목록 조회 (최신순)
    List<PostDTO> getAllPosts(); // 모든 공고를 최신순으로 조회


}
