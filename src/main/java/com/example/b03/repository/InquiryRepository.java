// 11. 1:1 문의
package com.example.b03.repository;

import com.example.b03.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
}
