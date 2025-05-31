// 2. 회원
// 2. Member
package com.example.b03.repository;

import com.example.b03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByLoginId(String loginId);
}