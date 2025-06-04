package com.example.b03.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostJobCategoryDTO {

    private Integer postId;
    private Integer jobCategoryId;

    private String postTitle;        // Post.title
    private String jobCategoryName;  // JobCategory.name


    public static PostJobCategoryDTO fromEntity(com.example.b03.domain.PostJobCategory entity) {
        return PostJobCategoryDTO.builder()
                .postId(entity.getPostId())
                .jobCategoryId(entity.getJobCategoryId())
                .postTitle(entity.getPost() != null ? entity.getPost().getTitle() : null)
                .jobCategoryName(entity.getJobCategory() != null ? entity.getJobCategory().getName() : null)
                .build();
    }

}
