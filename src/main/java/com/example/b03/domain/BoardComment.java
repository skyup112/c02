package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "post_id", nullable = false)
    private BoardPost boardPost;

    @ManyToOne
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Lob
    @Column(nullable = false)
    private String content;

    // 댓글 내용 변경 메서드
    public void changeContent(String content) {
        this.content = content;
    }
}
