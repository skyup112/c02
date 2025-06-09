package com.example.b03.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BoardPageResponseDTO<E> {

    private List<E> dtoList; // 게시글 DTO 목록
    private int totalElements; // 전체 게시글 수
    private int page; // 현재 페이지 번호
    private int size; // 페이지 당 항목 수

    // --- 여기부터 추가해야 할 필드들입니다 ---
    private int start; // 페이지 번호 목록의 시작 번호
    private int end; // 페이지 번호 목록의 끝 번호
    private boolean prev; // 이전 페이지 존재 여부
    private boolean next; // 다음 페이지 존재 여부
    private List<Integer> pageNumList; // 페이지 번호 목록 (옵션, 템플릿에서 #numbers.sequence 사용하면 필요 없을 수도 있지만, 컨트롤러에서 계산해서 넘겨주는 경우가 많음)
    // --- 여기까지 추가해야 할 필드들입니다 ---

    // 검색 관련 정보 추가
    private String type;
    private String keyword;

    // 빌더를 통해 모든 필드를 초기화할 수 있도록 생성자도 업데이트해야 합니다.
    @Builder(builderMethodName = "withAll")
    public BoardPageResponseDTO(List<E> dtoList, int totalElements, int page, int size,
                                int start, int end, boolean prev, boolean next, List<Integer> pageNumList, // 추가된 필드들
                                String type, String keyword) {
        this.dtoList = dtoList;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.start = start;
        this.end = end;
        this.prev = prev;
        this.next = next;
        this.pageNumList = pageNumList; // 필요시 초기화
        this.type = type;
        this.keyword = keyword;
    }
}