package com.example.b03.service;

import com.example.b03.dto.PostDTO;
import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO dto);
    PostDTO updatePost(PostDTO dto);
    void deletePost(Integer postId);
    PostDTO getPost(Integer postId);
    List<PostDTO> getPostsByCompany(Integer memberNo); // 기업 회원 전용
}
