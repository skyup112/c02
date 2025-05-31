// 4. 직무 카테고리
package com.example.b03.repository;

import com.example.b03.domain.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
}
