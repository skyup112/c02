package com.example.b03.repository;

import com.example.b03.domain.Inquiry;
import com.example.b03.domain.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Integer>, QuerydslPredicateExecutor<InquiryComment> {

    // Inquiry 객체로 검색하는 메서드. (InquiryCommentServiceImpl에서 사용)
    List<InquiryComment> findByInquiryAndIsDeletedFalseOrderByCreatedAtAsc(Inquiry inquiry);

    // ⭐ 참고: InquiryId로 직접 조회하고 페이징을 원한다면 다음 메서드를 추가하는 것이 좋음 (현재는 사용하지 않음)
    // Page<InquiryComment> findByInquiry_InquiryIdAndIsDeletedFalseOrderByCreatedAtAsc(Integer inquiryId, Pageable pageable);
}