// 12. 1:1 문의 답변
package com.example.b03.repository;

import com.example.b03.domain.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Integer> {
}
