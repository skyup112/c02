package com.example.b03.service;

import com.example.b03.domain.JobCategory;
import com.example.b03.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepository repository;

    @Override
    public List<JobCategory> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public JobCategory getCategoryById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
    }

    @Override
    public JobCategory createCategory(JobCategory category) {
        return repository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        repository.deleteById(id);
    }
}
