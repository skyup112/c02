package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_comments")
@Data
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
