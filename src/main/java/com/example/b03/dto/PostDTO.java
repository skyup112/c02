package com.example.b03.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer postId;
    private Integer memberNo; // CompanyInfo 기준
    private String title;
    private String description;
    private String salary;
    private String address;
    private LocalDateTime postedDate;
    private LocalDateTime updatedDate;
    private LocalDate deadline;

    private Integer companyMemberNo;
}
