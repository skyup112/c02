package com.example.b03.service;

import com.example.b03.domain.JobCategory;

import java.util.List;

public interface JobCategoryService {
    List<JobCategory> getAllCategories();
    JobCategory getCategoryById(Integer id);
    JobCategory createCategory(JobCategory category);
    void deleteCategory(Integer id);
}
