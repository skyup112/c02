// 10. BoardComment
package com.example.b03.repository;

import com.example.b03.domain.BoardComment;
import com.example.b03.domain.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {

    Page<BoardComment> findByBoardPost_PostIdOrderByCreatedAtAsc(Integer postId, Pageable pageable);

    List<BoardComment> findByBoardPost_PostIdOrderByCreatedAtAsc(Integer postId);

}

//     @Query("SELECT bc FROM BoardComment bc WHERE bc.boardPost.postId = :postId ORDER BY bc.commentId DESC")
//    List<BoardComment> findCommentsByBoardPostIdOrderByCommentIdDesc(@Param("postId") Integer postId);

//  List<BoardComment> findByBoardPost(BoardPost post);