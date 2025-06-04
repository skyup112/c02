package com.example.b03.service;

import com.example.b03.domain.JobCategory;
import com.example.b03.dto.JobCategoryDTO;
import com.example.b03.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<JobCategoryDTO> getAll() {
        List<JobCategory> categories = jobCategoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, JobCategoryDTO.class))
                .collect(Collectors.toList());
    }
}

