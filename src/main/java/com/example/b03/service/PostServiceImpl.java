package com.example.b03.service;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Member;
import com.example.b03.domain.Post;
import com.example.b03.dto.PostDTO;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.PostRepository;
import com.example.b03.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public PostDTO createPost(PostDTO dto) {
        // 1. 기업 회원(Member) 조회
        Member member = memberRepository.findById(dto.getCompanyMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 2. 해당 회원의 기업 정보(CompanyInfo) 조회
        CompanyInfo companyInfo = companyInfoRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("기업 정보가 존재하지 않습니다."));

        // 3. Post 엔티티 생성
        Post post = Post.builder()
                .company(companyInfo)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .salary(dto.getSalary())
                .address(dto.getAddress())
                .deadline(dto.getDeadline())
                .postedDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // 4. 저장 및 반환
        Post saved = postRepository.save(post);
        return toDTO(saved);
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

    @Override
    public List<PostDTO> getAllPosts() {
        // 모든 공고를 최신순으로 가져오는 로직
        return postRepository.findAllByOrderByPostedDateDesc() // 최신순으로 정렬
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Post -> PostDTO 변환
    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .description(post.getDescription())
                .salary(post.getSalary())
                .address(post.getAddress())
                .deadline(post.getDeadline())
                .postedDate(post.getPostedDate())
                .updatedDate(post.getUpdatedDate())
                .companyMemberNo(post.getCompany().getMemberNo()) // 회사 회원 번호 추가
                .build();
    }
}
