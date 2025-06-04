// 4. JobCategory
package com.example.b03.repository;

import com.example.b03.domain.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> { ;
}
