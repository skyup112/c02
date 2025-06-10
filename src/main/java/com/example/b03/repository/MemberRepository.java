// 2. 회원
// 2. Member
package com.example.b03.repository;

import com.example.b03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId")
    Optional<Member> findByLoginId(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);

    @Query("SELECT m FROM Member m JOIN FETCH m.membershipType WHERE m.memberNo = :id")
    Optional<Member> findByIdWithMembershipType(@Param("id") Integer id);

    @Query("SELECT m FROM Member m JOIN FETCH m.membershipType WHERE m.loginId = :loginId")
    Optional<Member> findByLoginIdWithMembershipType(@Param("loginId") String loginId);

}