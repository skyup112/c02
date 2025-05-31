// 9. BoardPost
package com.example.b03.repository;

import com.example.b03.domain.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Integer> {
}