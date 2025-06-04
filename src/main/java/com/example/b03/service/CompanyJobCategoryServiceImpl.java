package com.example.b03.service;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.CompanyJobCategory;
import com.example.b03.domain.JobCategory;
import com.example.b03.dto.CompanyJobCategoryDTO;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.CompanyJobCategoryRepository;
import com.example.b03.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CompanyJobCategoryServiceImpl implements CompanyJobCategoryService {

    private final CompanyJobCategoryRepository companyJobCategoryRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final JobCategoryRepository jobCategoryRepository;

    @Override
    public void registerCategories(Integer memberNo, List<Integer> jobCategoryIds) {
        CompanyInfo company = companyInfoRepository.findById(memberNo)
                .orElseThrow(() -> new NoSuchElementException("회사 정보를 찾을 수 없습니다."));

        // 기존 직무 카테고리 삭제
        companyJobCategoryRepository.deleteAll(companyJobCategoryRepository.findAllByCompany_MemberNo(memberNo));

        // 새로 등록
        List<CompanyJobCategory> newMappings = jobCategoryIds.stream()
                .map(jobCategoryId -> {
                    JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId)
                            .orElseThrow(() -> new NoSuchElementException("직무 카테고리를 찾을 수 없습니다."));

                    return CompanyJobCategory.builder()
                            .memberNo(memberNo)
                            .jobCategoryId(jobCategoryId)
                            .company(company)
                            .jobCategory(jobCategory)
                            .build();
                }).toList();

        companyJobCategoryRepository.saveAll(newMappings);
    }

    @Override
    public List<CompanyJobCategoryDTO> getCategoriesByMemberNo(Integer memberNo) {
        return companyJobCategoryRepository.findAllByCompany_MemberNo(memberNo).stream()
                .map(mapping -> CompanyJobCategoryDTO.builder()
                        .memberNo(mapping.getMemberNo())
                        .jobCategoryId(mapping.getJobCategoryId())
                        .jobCategoryName(mapping.getJobCategory().getName())
                        .build())
                .toList();
//        .map(mapping -> modelMapper.map(mapping, CompanyJobCategoryDTO.class));

    }

    @Override
    public void deleteCategoriesByMemberNo(Integer memberNo) {
        List<CompanyJobCategory> existing = companyJobCategoryRepository.findAllByCompany_MemberNo(memberNo);
        companyJobCategoryRepository.deleteAll(existing);
    }
}
