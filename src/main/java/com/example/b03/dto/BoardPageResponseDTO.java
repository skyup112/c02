package com.example.b03.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page; // Spring Data Page 임포트

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@ToString
public class BoardPageResponseDTO<E> {

    private List<E> dtoList; // 게시글 DTO 목록
    private int totalElements; // 전체 게시글 수
    private int page; // 현재 페이지 번호 (1부터 시작)
    private int size; // 페이지 당 항목 수
    private int totalPage; // 전체 페이지 수

    private int start; // 페이지 번호 목록의 시작 번호
    private int end; // 페이지 번호 목록의 끝 번호
    private boolean prev; // 이전 페이지 존재 여부
    private boolean next; // 다음 페이지 존재 여부
    private List<Integer> pageNumList; // 페이지 번호 목록

    private String type;
    private String keyword;

    // BoardServiceImpl에서 Page<E> 객체를 받아 페이징 정보를 자동으로 계산하는 생성자 추가
    public BoardPageResponseDTO(Page<?> result, List<E> dtoList, BoardPageRequestDTO requestDTO) {
        this.dtoList = dtoList;
        this.totalElements = (int) result.getTotalElements();
        this.page = requestDTO.getPage(); // 요청받은 페이지 번호 (1부터 시작)
        this.size = requestDTO.getSize();
        this.totalPage = result.getTotalPages(); // Page 객체에서 총 페이지 수 가져옴

        // 페이지 번호 목록 계산
        int endPage = (int)(Math.ceil(this.page / 10.0)) * 10; // 현재 페이지 그룹의 끝 페이지 (예: 10, 20, 30)
        int startPage = endPage - 9; // 현재 페이지 그룹의 시작 페이지 (예: 1, 11, 21)

        // 실제 마지막 페이지를 초과하지 않도록 조정
        if (endPage > this.totalPage) {
            endPage = this.totalPage;
        }

        this.prev = startPage > 1; // 시작 페이지가 1보다 크면 이전 페이지가 있음
        this.next = endPage < this.totalPage; // 끝 페이지가 전체 페이지보다 작으면 다음 페이지가 있음

        this.start = startPage;
        this.end = endPage;

        this.pageNumList = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());

        this.type = requestDTO.getType();
        this.keyword = requestDTO.getKeyword();
    }

    // 기존 빌더 메소드는 유지하되, 위 생성자를 통해 객체를 생성하는 것을 권장
    @Builder(builderMethodName = "withAll")
    public BoardPageResponseDTO(List<E> dtoList, int totalElements, int page, int size,
                                int totalPage,
                                int start, int end, boolean prev, boolean next, List<Integer> pageNumList,
                                String type, String keyword) {
        this.dtoList = dtoList;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPage = totalPage;
        this.start = start;
        this.end = end;
        this.prev = prev;
        this.next = next;
        this.pageNumList = pageNumList;
        this.type = type;
        this.keyword = keyword;
    }
}