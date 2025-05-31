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

    @ManyToOne
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;
}

