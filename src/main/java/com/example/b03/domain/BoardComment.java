package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "board_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private BoardPost boardPost;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @Lob
    @Column(nullable = false)
    private String content;
}
