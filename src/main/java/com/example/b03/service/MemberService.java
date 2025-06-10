package com.example.b03.service;

import com.example.b03.dto.MemberDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    MemberDTO register(MemberDTO memberDTO);
    Optional<MemberDTO> findByLoginId(String loginId);
    Optional<MemberDTO> getByMemberNo(Integer memberNo);
    void delete(Integer memberNo);
    MemberDTO update(MemberDTO dto);

    MemberDTO getMemberByLoginId(String loginId);
    List<MemberDTO> getAllMembers();
    MemberDTO createMember(MemberDTO memberDTO);
    MemberDTO updateMember(Integer memberNo, MemberDTO memberDTO);
    boolean isLoginIdDuplicate(String loginId);
    void changePassword(Integer memberNo, String newPassword);
    void deactivateMember(Integer memberNo);
    boolean isValidPassword(String password);
    PageResponseDTO<MemberDTO> getPagedMembers(PageRequestDTO requestDTO);
    MemberDTO getLoginMember();

}
