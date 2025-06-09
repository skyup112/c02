package com.example.b03.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPageRequestDTO { // 클래스 이름 변경

    @Builder.Default
    private int page = 1; // 기본 페이지 번호

    @Builder.Default
    private int size = 10; // 기본 페이지 당 항목 수

    private String type; // 검색 타입 (t: 제목, c: 내용, w: 작성자, tc: 제목+내용, tcw: 제목+내용+작성자)
    private String keyword; // 검색 키워드

    public Pageable getPageable(Sort sort) {
        // 페이지 번호는 0부터 시작하므로 -1 처리
        return PageRequest.of(this.page - 1, this.size, sort);
    }
}