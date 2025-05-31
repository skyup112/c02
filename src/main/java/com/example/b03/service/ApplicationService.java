package com.example.b03.service;

import com.example.b03.dto.ApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    // 기본 CRUD
    ApplicationDTO create(ApplicationDTO dto);
    ApplicationDTO update(ApplicationDTO dto);
    void delete(Integer applicationId);
    ApplicationDTO getById(Integer applicationId);

    // 공고 및 회원별 지원 이력 조회
    List<ApplicationDTO> getByMemberNo(Integer memberNo);
    List<ApplicationDTO> getByPostId(Integer postId);

    // 지원서 등록 (파일 포함)
    void applyToPost(ApplicationDTO dto, MultipartFile file);

    // 개인 회원: 나의 지원서 목록
    List<ApplicationDTO> getApplicationsByMember(Integer memberNo);

    // 기업 회원: 내가 올린 공고 전체에 대한 지원 이력
    List<ApplicationDTO> getApplicationsByCompany(Integer companyMemberNo);

    // 기업 회원: 특정 공고의 지원 이력 (접근 검증 포함)
    List<ApplicationDTO> getApplicationsByPostAndCompany(Integer postId, Integer companyMemberNo);

    // 중복 지원 여부 확인
    boolean isDuplicateApplication(Integer postId, Integer memberNo);

    // 기업회원: 지원자 이름, 전화번호 포함 지원 목록 반환
    List<Map<String, Object>> getApplicationsWithMemberInfoByPostAndCompany(Integer postId, Integer companyMemberNo);
}
