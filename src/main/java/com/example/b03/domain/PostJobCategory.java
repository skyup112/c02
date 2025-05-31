package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_job_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostJobCategoryId.class)
public class PostJobCategory {
    @Id
    private Integer postId;

    @Id
    private Integer jobCategoryId;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "job_category_id", insertable = false, updatable = false)
    private JobCategory jobCategory;
}
