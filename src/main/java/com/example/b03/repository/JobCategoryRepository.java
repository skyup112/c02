package com.example.b03.repository;


import com.example.b03.domain.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
    // 기본적인 CRUD 외에 필요한 쿼리가 있다면 여기에 추가하면 됩니다.
}