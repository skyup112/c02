// 10. BoardComment
package com.example.b03.repository;

import com.example.b03.domain.BoardComment;
import com.example.b03.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {
    List<BoardComment> findByBoardPost(BoardPost post);
}
