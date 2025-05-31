package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "inquiry_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @ManyToOne
    @JoinColumn(name = "admin_no")
    private Member admin;

    @Lob
    @Column(nullable = false)
    private String content;
}


