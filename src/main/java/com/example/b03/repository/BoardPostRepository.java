package com.example.b03.repository;

import com.example.b03.domain.BoardPost;
import com.example.b03.repository.search.BoardSearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardPostRepository extends JpaRepository<BoardPost, Integer>, BoardSearchRepository, QuerydslPredicateExecutor<BoardPost> {

    // JpaRepository 기본 CRUD와 페이징 기능 제공
    // BoardSearchRepository는 직접 정의할 검색 메소드를 포함합니다.
    // QuerydslPredicateExecutor는 Querydsl Predicate를 받아 동적 쿼리 실행을 지원합니다.

}
