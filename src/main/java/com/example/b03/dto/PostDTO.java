package com.example.b03.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer postId;
    private Integer memberNo;// CompanyInfo 기준

    private String title;
    private String description;
    private String salary;

    private Integer companyMemberNo;
    private String companyName;
    private String address;
    private String companyPhone;

    private LocalDateTime postedDate;
    private LocalDateTime updatedDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAtEntity;

    private LocalDate deadline;

    private Boolean isDeleted;

    private List<String> jobCategoryNames;
}
