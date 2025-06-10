package com.example.b03.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardPostDTO {

    private Integer postId;

    @NotEmpty(message = "제목을 입력하세요.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotEmpty(message = "내용을 입력하세요.")
    private String content;

    @NotEmpty(message = "작성자 이름은 필수 입력 사항입니다.")     // member 정보에서 자동으로 가져올 것이기 때문에 안 쓰는 걸 추천 ( 게시글 등록 시 DTO에 직접 입력되는 값이 아니라서)
    private String writerName;  // Member 에서 추출

    private LocalDateTime createdAt;  // BaseEntity에서 상속

    private Boolean isDeleted;  // BaseEntity에서 상속
}

