package com.example.b03.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Integer applicationId;
    private Integer postId;
    private Integer memberNo;
    private String filePath;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;

    private String postTitle;

    private LocalDate deadline;

}