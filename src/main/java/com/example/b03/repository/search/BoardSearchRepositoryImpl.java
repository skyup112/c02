package com.example.b03.repository.search;

import com.example.b03.domain.BoardPost;
import com.example.b03.domain.QBoardPost;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchRepositoryImpl extends QuerydslRepositorySupport implements BoardSearchRepository {

    public BoardSearchRepositoryImpl() {
        super(BoardPost.class); // 부모 클래스에 엔티티 클래스 전달
    }

    @Override
    public Page<BoardPost> searchAll(String type, String keyword, Pageable pageable) {

        QBoardPost boardPost = QBoardPost.boardPost; // QBoardPost 인스턴스 생성

        // Querydsl 쿼리 생성
        // from(boardPost)는 SELECT * FROM board_post 와 유사
        // where 절을 구성하는 BooleanBuilder를 생성
        BooleanBuilder condition = new BooleanBuilder();

        // postId가 0보다 큰 경우에 대한 기본 조건 (모든 게시글 포함)
        condition.and(boardPost.postId.gt(0));

        // 검색 타입과 키워드가 있는 경우 검색 조건 추가
        if (type != null && keyword != null && !keyword.trim().isEmpty()) {
            switch (type) {
                case "t": // 제목 검색
                    condition.and(boardPost.title.contains(keyword));
                    break;
                case "c": // 내용 검색
                    condition.and(boardPost.content.contains(keyword));
                    break;
                case "w": // 작성자 ID 검색
                    condition.and(boardPost.member.loginId.contains(keyword));
                    break;
                case "tc": // 제목 또는 내용 검색
                    condition.and(boardPost.title.contains(keyword).or(boardPost.content.contains(keyword)));
                    break;
                case "tcw": // 제목, 내용 또는 작성자 ID 검색
                    condition.and(boardPost.title.contains(keyword)
                            .or(boardPost.content.contains(keyword))
                            .or(boardPost.member.loginId.contains(keyword)));
                    break;
            }
        }

        // 쿼리 실행 및 페이징 처리
        // QuerydslRepositorySupport가 제공하는 from, where 등을 사용
        List<BoardPost> content = from(boardPost)
                .where(condition)
                .orderBy(boardPost.createdAt.desc()) // 최신 게시글이 먼저 오도록 정렬
                .offset(pageable.getOffset()) // 페이징 시작 오프셋
                .limit(pageable.getPageSize()) // 페이지 당 항목 수
                .fetch(); // 결과 리스트 가져오기

        // 전체 개수 (페이징 정보 계산용)
        long total = from(boardPost)
                .where(condition)
                .fetchCount(); // 조건에 맞는 전체 개수 가져오기

        // Page 객체로 래핑하여 반환
        return new PageImpl<>(content, pageable, total);
    }
}
