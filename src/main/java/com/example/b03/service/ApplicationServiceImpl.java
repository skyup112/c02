package com.example.b03.service;

import com.example.b03.domain.Application;
import com.example.b03.domain.Member;
import com.example.b03.domain.Post;
import com.example.b03.dto.ApplicationDTO;
import com.example.b03.repository.ApplicationRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.PostRepository;
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
        Member member = getMember(dto.getMemberNo());
        Post post = getPost(dto.getPostId());

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
        Member member = getMember(dto.getMemberNo());
        Post post = getPost(dto.getPostId());

        if (applicationRepository.existsByPostAndMember(post, member)) {
            throw new IllegalStateException("이미 지원한 공고입니다.");
        }

        String filePath = file != null && !file.isEmpty() ? saveFile(file) : null;

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
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // ✅ DB에는 브라우저 접근 가능한 경로 저장 (예: "/uploads/파일명.pdf")
        return "/uploads/" + fileName;
    }

    @Override
    public List<ApplicationDTO> getApplicationsByCompany(Integer companyMemberNo) {
        Member company = getMember(companyMemberNo);
        return applicationRepository.findByPost_Company_Member(company)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isDuplicateApplication(Integer postId, Integer memberNo) {
        return applicationRepository.existsByPostAndMember(getPost(postId), getMember(memberNo));
    }

    @Override
    public List<ApplicationDTO> getApplicationsByPostAndCompany(Integer postId, Integer companyMemberNo) {
        Post post = getPost(postId);
        if (!post.getCompany().getMember().getMemberNo().equals(companyMemberNo)) {
            throw new IllegalArgumentException("해당 공고에 접근할 수 없습니다.");
        }

        return applicationRepository.findByPost_PostId(postId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsByMember(Integer memberNo) {
        return applicationRepository.findByMember_MemberNo(memberNo).stream()
                .sorted(Comparator.comparing(Application::getSubmittedAt).reversed())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getApplicationsWithMemberInfoByPostAndCompany(Integer postId, Integer companyMemberNo) {
        Post post = getPost(postId);
        if (!post.getCompany().getMember().getMemberNo().equals(companyMemberNo)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        return applicationRepository.findByPost_PostId(postId).stream().map(application -> {
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

    private Member getMember(Integer memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private Post getPost(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고가 존재하지 않습니다."));
    }

    private ApplicationDTO toDTO(Application application) {
        return ApplicationDTO.builder()
                .applicationId(application.getApplicationId())
                .memberNo(application.getMember().getMemberNo())
                .postId(application.getPost().getPostId())
                .postTitle(application.getPost().getTitle())
                .filePath(application.getFilePath())
                .submittedAt(application.getSubmittedAt())
                .updatedAt(application.getUpdatedAt())
                .deadline(application.getPost().getDeadline()) // 🔹 여기에서 Post의 마감일 포함
                .build();
    }

    @Override
    public List<ApplicationDTO> getApplicationsByPost(Integer postId) {
        List<Application> entities = applicationRepository.findByPost_PostId(postId);

        return entities.stream()
                .map(application -> ApplicationDTO.builder()
                        .applicationId(application.getApplicationId())
                        .postId(application.getPost().getPostId())
                        .postTitle(application.getPost().getTitle())
                        .memberNo(application.getMember().getMemberNo())
                        .filePath(application.getFilePath())
                        .submittedAt(application.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
