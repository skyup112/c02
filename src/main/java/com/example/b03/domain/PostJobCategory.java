package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_job_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostJobCategoryId.class)
public class PostJobCategory {

    @Id
    private Integer postId;

    @Id
    private Integer jobCategoryId;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("jobCategoryId")
    @JoinColumn(name = "job_category_id")
    private JobCategory jobCategory;
}
