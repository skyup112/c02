package com.example.b03.service;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Post;
import com.example.b03.dto.PostDTO;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.PostRepository;
import com.example.b03.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CompanyInfoRepository companyInfoRepository;

    @Override
    public PostDTO createPost(PostDTO dto) {
        CompanyInfo company = companyInfoRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("기업 회원 정보가 없습니다."));

        Post post = Post.builder()
                .company(company)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .salary(dto.getSalary())
                .address(dto.getAddress())
                .postedDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .deadline(dto.getDeadline())
                .build();

        return toDTO(postRepository.save(post));
    }

    @Override
    public PostDTO updatePost(PostDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setSalary(dto.getSalary());
        post.setAddress(dto.getAddress());
        post.setUpdatedDate(LocalDateTime.now());
        post.setDeadline(dto.getDeadline());

        return toDTO(postRepository.save(post));
    }

    @Override
    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostDTO getPost(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));
        return toDTO(post);
    }

    @Override
    public List<PostDTO> getPostsByCompany(Integer memberNo) {
        return postRepository.findByCompany_Member_MemberNo(memberNo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .memberNo(post.getCompany().getMember().getMemberNo())
                .title(post.getTitle())
                .description(post.getDescription())
                .salary(post.getSalary())
                .address(post.getAddress())
                .postedDate(post.getPostedDate())
                .updatedDate(post.getUpdatedDate())
                .deadline(post.getDeadline())
                .build();
    }
}
