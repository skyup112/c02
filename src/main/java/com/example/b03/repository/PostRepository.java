// 6. 채용 공고
package com.example.b03.repository;

import com.example.b03.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
