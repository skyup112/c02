// 1. MembershipType
package com.example.b03.repository;

import com.example.b03.domain.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTypeRepository extends JpaRepository<MembershipType, Byte> {
}
