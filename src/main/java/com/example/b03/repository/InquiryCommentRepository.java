// 12. InquiryComment
package com.example.b03.repository;

import com.example.b03.domain.Inquiry;
import com.example.b03.domain.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Integer> {
    List<InquiryComment> findByInquiry(Inquiry inquiry);
}