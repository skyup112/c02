package com.example.b03.service;

import com.example.b03.domain.Application;
import com.example.b03.domain.Member;
import com.example.b03.domain.Post;
import com.example.b03.dto.ApplicationDTO;
import com.example.b03.repository.ApplicationRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.PostRepository;
import com.example.b03.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Value("${file.upload-dir:/uploads/}")
    private String uploadDir;

    @Override
    public ApplicationDTO create(ApplicationDTO dto) {
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        Application application = Application.builder()
                .member(member)
                .post(post)
                .filePath(dto.getFilePath())
                .submittedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toDTO(applicationRepository.save(application));
    }

    @Override
    public ApplicationDTO update(ApplicationDTO dto) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("지원서가 존재하지 않습니다."));

        application.setFilePath(dto.getFilePath());
        application.setUpdatedAt(LocalDateTime.now());

        return toDTO(applicationRepository.save(application));
    }

    @Override
    public void delete(Integer applicationId) {
        applicationRepository.deleteById(applicationId);
    }

    @Override
    public ApplicationDTO getById(Integer applicationId) {
        return applicationRepository.findById(applicationId)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("지원서가 존재하지 않습니다."));
    }

    @Override
    public List<ApplicationDTO> getByMemberNo(Integer memberNo) {
        return applicationRepository.findByMember_MemberNo(memberNo)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getByPostId(Integer postId) {
        return applicationRepository.findByPost_PostId(postId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void applyToPost(ApplicationDTO dto, MultipartFile file) {
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        if (applicationRepository.existsByPostAndMember(post, member)) {
            throw new IllegalStateException("이미 지원한 공고입니다.");
        }

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = saveFile(file);
        }

        Application application = Application.builder()
                .member(member)
                .post(post)
                .filePath(filePath)
                .submittedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
    }

    private String saveFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + File.separator + fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
        return dest.getAbsolutePath();
    }

    @Override
    public List<ApplicationDTO> getApplicationsByMember(Integer memberNo) {
        return applicationRepository.findByMember_MemberNo(memberNo)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsByCompany(Integer companyMemberNo) {
        Member company = memberRepository.findById(companyMemberNo)
                .orElseThrow(() -> new IllegalArgumentException("기업 회원이 존재하지 않습니다."));
        return applicationRepository.findByPost_Company_Member(company)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isDuplicateApplication(Integer postId, Integer memberNo) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));
        return applicationRepository.existsByPostAndMember(post, member);
    }

    @Override
    public List<ApplicationDTO> getApplicationsByPostAndCompany(Integer postId, Integer companyMemberNo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        if (!post.getCompany().getMember().getMemberNo().equals(companyMemberNo)) {
            throw new IllegalArgumentException("해당 공고에 접근할 수 없습니다.");
        }

        return applicationRepository.findByPost_PostId(postId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getApplicationsWithMemberInfoByPostAndCompany(Integer postId, Integer companyMemberNo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));

        if (!post.getCompany().getMember().getMemberNo().equals(companyMemberNo)) {
            throw new IllegalArgumentException("해당 공고에 접근 권한이 없습니다.");
        }

        List<Application> applications = applicationRepository.findByPost_PostId(postId);

        return applications.stream().map(application -> {
            Map<String, Object> map = new HashMap<>();
            map.put("applicationId", application.getApplicationId());
            map.put("postId", application.getPost().getPostId());
            map.put("memberNo", application.getMember().getMemberNo());
            map.put("applicantName", application.getMember().getName());
            map.put("applicantPhone", application.getMember().getPhone());
            map.put("filePath", application.getFilePath());
            map.put("submittedAt", application.getSubmittedAt());
            map.put("updatedAt", application.getUpdatedAt());
            return map;
        }).collect(Collectors.toList());
    }

    private ApplicationDTO toDTO(Application application) {
        return ApplicationDTO.builder()
                .applicationId(application.getApplicationId())
                .memberNo(application.getMember().getMemberNo())
                .postId(application.getPost().getPostId())
                .filePath(application.getFilePath())
                .submittedAt(application.getSubmittedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
