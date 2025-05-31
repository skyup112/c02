// 2. 회원
package com.example.b03.repository;

import com.example.b03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    // 아이디(id)로 회원 조회할 때 사용할 수 있는 메서드 예시
    Optional<Member> findById(String id);
}