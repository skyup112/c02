// 7. PostJobCategory
package com.example.b03.repository;

import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostJobCategoryRepository extends JpaRepository<PostJobCategory, PostJobCategoryId> {
    List<PostJobCategory> findAllByPost_PostId(Integer postId);
}
