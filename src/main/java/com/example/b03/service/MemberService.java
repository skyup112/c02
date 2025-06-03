package com.example.b03.service;

import com.example.b03.dto.MemberDTO;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    // 기존 메서드
    MemberDTO register(MemberDTO memberDTO);
    Optional<MemberDTO> findByLoginId(String loginId);
    Optional<MemberDTO> getByMemberNo(Integer memberNo);
    void delete(Integer memberNo);
    MemberDTO update(MemberDTO dto);

    // 추가 메서드
    MemberDTO getMemberByLoginId(String loginId);
    List<MemberDTO> getAllMembers();
    MemberDTO createMember(MemberDTO memberDTO);
    MemberDTO updateMember(Integer memberNo, MemberDTO memberDTO);
    boolean checkDuplicateLoginId(String loginId);
    void changePassword(Integer memberNo, String newPassword);
    void deactivateMember(Integer memberNo);
    boolean isValidPassword(String password);
}
