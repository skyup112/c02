package com.example.b03.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoDTO {
    private Integer memberNo;
    private String companyName;
    private LocalDate foundedDate;
    private Integer employeeCount;
    private Long revenue;
    private String techStack;
    private String location;
    private String homepageUrl;
    private String description;
}
