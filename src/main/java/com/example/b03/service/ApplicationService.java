package com.example.b03.service;

import com.example.b03.dto.ApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ApplicationService {
    ApplicationDTO create(ApplicationDTO dto);
    ApplicationDTO update(ApplicationDTO dto);
    void delete(Integer applicationId);
    ApplicationDTO getById(Integer applicationId);

    List<ApplicationDTO> getByMemberNo(Integer memberNo);
    List<ApplicationDTO> getByPostId(Integer postId);

    void applyToPost(ApplicationDTO dto, MultipartFile file);

    List<ApplicationDTO> getApplicationsByMember(Integer memberNo);
    List<ApplicationDTO> getApplicationsByCompany(Integer companyMemberNo);
    List<ApplicationDTO> getApplicationsByPostAndCompany(Integer postId, Integer companyMemberNo);
    List<ApplicationDTO> getApplicationsByPost(Integer postId); // ✅ 이 줄 추가됨
    boolean isDuplicateApplication(Integer postId, Integer memberNo);
    List<Map<String, Object>> getApplicationsWithMemberInfoByPostAndCompany(Integer postId, Integer companyMemberNo);
}