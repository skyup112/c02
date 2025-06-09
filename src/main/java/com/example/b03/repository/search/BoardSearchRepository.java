package com.example.b03.repository.search;

import com.example.b03.domain.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearchRepository {


    // 검색 타입(t, c, w 등)과 키워드를 받아 페이징된 결과를 반환하는 메소드
    Page<BoardPost> searchAll(String type, String keyword, Pageable pageable);
}
