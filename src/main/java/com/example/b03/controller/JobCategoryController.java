package com.example.b03.controller;

import com.example.b03.domain.JobCategory;
import com.example.b03.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryRepository jobCategoryRepository;

    // ✅ 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<JobCategory>> getAll() {
        return ResponseEntity.ok(jobCategoryRepository.findAll());
    }

    // ✅ 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<JobCategory> getById(@PathVariable Integer id) {
        return jobCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 생성
    @PostMapping
    public ResponseEntity<JobCategory> create(@RequestBody JobCategory category) {
        return ResponseEntity.ok(jobCategoryRepository.save(category));
    }

    // ✅ 수정
    @PutMapping("/{id}")
    public ResponseEntity<JobCategory> update(@PathVariable Integer id, @RequestBody JobCategory updatedCategory) {
        return jobCategoryRepository.findById(id).map(existing -> {
            existing.setName(updatedCategory.getName());
            return ResponseEntity.ok(jobCategoryRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!jobCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
