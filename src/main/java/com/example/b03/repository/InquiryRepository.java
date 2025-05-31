// 11. Inquiry
package com.example.b03.repository;

import com.example.b03.domain.Inquiry;
import com.example.b03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    List<Inquiry> findByMember(Member member);
}
