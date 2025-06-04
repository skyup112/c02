package com.example.b03.service;

import com.example.b03.domain.MembershipType;
import com.example.b03.dto.MembershipTypeDTO;
import com.example.b03.repository.MembershipTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // MariaDB 그대로 사용
@Transactional
@Commit
public class MembershipTypeServiceImplTest {

    @Autowired
    private MembershipTypeService membershipTypeService;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @BeforeEach
    void setUp() {
        // 테스트용 데이터 3개 삽입 (관리자, 개인회원, 기업회원)
        if (membershipTypeRepository.findAll().isEmpty()) {
            membershipTypeRepository.save(new MembershipType((byte) 1, "관리자"));
            membershipTypeRepository.save(new MembershipType((byte) 3, "개인회원"));
            membershipTypeRepository.save(new MembershipType((byte) 2, "기업회원"));
        }
    }

    @Test
    void testGetAll() {
        List<MembershipTypeDTO> list = membershipTypeService.getAll();
        assertEquals(2, list.size());
        System.out.println("✅ 전체 멤버십 타입 조회 성공: " + list);
    }

    @Test
    void testGetById() {
        MembershipTypeDTO dto = membershipTypeService.getById((byte) 3);
        assertNotNull(dto);
        assertEquals("개인회원", dto.getTypeName());
        System.out.println("✅ ID로 멤버십 타입 조회 성공: " + dto);
    }
}
