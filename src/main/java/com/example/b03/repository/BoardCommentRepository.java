// 10. 자유게시판 댓글
package com.example.b03.repository;

import com.example.b03.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {
}
