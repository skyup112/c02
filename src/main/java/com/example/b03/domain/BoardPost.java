package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "board_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

//    @Column(name = "like_count", nullable = false)
//    private int likeCount;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

//    public void increaseLikeCount() {
//        this.likeCount++;
//    }
//
//    public void decreaseLikeCount() {
//        if (this.likeCount > 0) {
//            this.likeCount--;
//        }

}

