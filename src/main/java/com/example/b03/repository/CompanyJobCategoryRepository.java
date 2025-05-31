// 5. 회사별 직무 카테고리
package com.example.b03.repository;

import com.example.b03.domain.CompanyJobCategory;
import com.example.b03.domain.CompanyJobCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyJobCategoryRepository extends JpaRepository<CompanyJobCategory, CompanyJobCategoryId> {
    List<CompanyJobCategory> findAllByCompany_MemberNo(Integer memberNo);
}
