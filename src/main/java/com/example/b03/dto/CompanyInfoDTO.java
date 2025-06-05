package com.example.b03.dto;

import com.example.b03.domain.CompanyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoDTO {
    private Integer memberNo;
    private String companyName;
    private LocalDate foundedDate;
    private Integer employeeCount;
    private Long revenue;
    private String techStack;
    private String homepageUrl;
    private String description;

    //  Member에서 가져온 값
    private String phone;
    private String address;

    private List<String> jobCategoryNames;

    public static CompanyInfoDTO fromEntity(CompanyInfo entity) {
        return CompanyInfoDTO.builder()
                .memberNo(entity.getMember().getMemberNo())
                .companyName(entity.getCompanyName())
                .foundedDate(entity.getFoundedDate())
                .employeeCount(entity.getEmployeeCount())
                .revenue(entity.getRevenue())
                .techStack(entity.getTechStack())
                .homepageUrl(entity.getHomepageUrl())
                .description(entity.getDescription())
                .address(entity.getMember().getAddress())  // 주소 매핑
                .phone(entity.getMember().getPhone())      // 전화번호 매핑
                .build();
    }
}

