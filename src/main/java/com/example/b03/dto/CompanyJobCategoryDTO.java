package com.example.b03.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJobCategoryDTO {
    private Integer memberNo;
    private Integer jobCategoryId;

    // 선택적으로 연관 정보 포함 가능
    private String companyName;    // CompanyInfo에서
    private String jobCategoryName; // JobCategory에서
}
