package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private CompanyInfo company;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(length = 100)
    private String salary;

    @Column(length = 100)
    private String location;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    private LocalDate deadline;
}

