// 7. 채용 공고별 직무
package com.example.b03.repository;
import com.example.b03.domain.PostJobCategory;
import com.example.b03.domain.PostJobCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJobCategoryRepository extends JpaRepository<PostJobCategory, PostJobCategoryId> {
}
