package com.example.b03.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardCommentDTO {

    private Integer commentId;
    private Integer postId;

    @NotEmpty(message = "댓글 내용을 작성해주세요.")
    private String content;

    private String writerName;      // Member에서 추출

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonIgnore         // 화면 출력 제외
    private LocalDateTime updatedAt;

    private Boolean isDeleted;      // BaseEntity에서 상속


}