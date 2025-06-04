package com.example.b03.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategoryDTO {
    private Integer jobCategoryId;
    private String name;

    // 예: 조회용으로만 사용하는 부가 정보
    // private Long companyCount;
}
