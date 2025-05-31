package com.example.b03.repository;

import com.example.b03.domain.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Integer> {
}
