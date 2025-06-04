package com.example.b03.service;

import com.example.b03.dto.CompanyJobCategoryDTO;

import java.util.List;

public interface CompanyJobCategoryService {

    void registerCategories(Integer memberNo, List<Integer> jobCategoryIds);

    List<CompanyJobCategoryDTO> getCategoriesByMemberNo(Integer memberNo);

    void deleteCategoriesByMemberNo(Integer memberNo);
}
