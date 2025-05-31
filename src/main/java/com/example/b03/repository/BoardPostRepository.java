// 9. 자유게시판 게시글
package com.example.b03.repository;

import com.example.b03.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Integer> {
}
