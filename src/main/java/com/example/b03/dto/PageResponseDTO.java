package com.example.b03.dto;

import lombok.*;

import java.util.List;
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    private List<E> dtoList;

    public int getCurrentPage() {
        return page;
    }

    public int getTotalPage() {
        return (int) Math.ceil((double) total / size);
    }

    // 기존 생성자 그대로 유지
    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        if (total <= 0) return;

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        this.end = (int) (Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;
        int last = (int) Math.ceil(total / (double) size);
        this.end = Math.min(end, last);
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }

}
