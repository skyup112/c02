package com.example.b03.service;

import com.example.b03.domain.Post;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO dto);
    void updatePost(PostDTO dto);
    void deletePost(Integer postId);
    PostDTO getPost(Integer postId);
    List<PostDTO> getPostsByCompany(Integer memberNo); // 기업 회원 전용

    List<PostDTO> getAllPosts(); // 모든 공고를 최신순으로 조회
    PostDTO getPostById(Integer postId);
    List<PostDTO> getPostsByCategoryId(Integer categoryId);
    PageResponseDTO<PostDTO> search(PageRequestDTO requestDTO, Integer categoryId);
    PageResponseDTO<PostDTO> getPagedPosts(PageRequestDTO requestDTO);

}
