package com.example.b03.service;

import com.example.b03.domain.Member;
import com.example.b03.domain.MembershipType;
import com.example.b03.dto.MemberDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public MemberDTO register(MemberDTO memberDTO) {
        MembershipType membershipType = membershipTypeRepository.findById(memberDTO.getMembershipTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Membership Type"));

        // 수동 매핑 (null 방지)
        Member member = Member.builder()
                .loginId(memberDTO.getLoginId())
                .password(memberDTO.getPassword())
                .name(memberDTO.getName())
                .birthDate(memberDTO.getBirthDate())
                .address(memberDTO.getAddress())
                .phone(memberDTO.getPhone())
                .membershipType(membershipType)
                .build();

        Member saved = memberRepository.save(member);
        return modelMapper.map(saved, MemberDTO.class);
    }

    @Override
    public Optional<MemberDTO> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(member -> modelMapper.map(member, MemberDTO.class));
    }

    @Override
    public MemberDTO update(MemberDTO dto) {
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (dto.getAddress() == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        member.setLoginId(dto.getLoginId());
        member.setPassword(dto.getPassword());
        member.setName(dto.getName());
        member.setBirthDate(dto.getBirthDate());
        member.setAddress(dto.getAddress());
        member.setPhone(dto.getPhone());

        if (dto.getMembershipTypeId() != null) {
            MembershipType membershipType = membershipTypeRepository.findById(dto.getMembershipTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Membership Type"));
            member.setMembershipType(membershipType);
        }

        Member updated = memberRepository.save(member);
        return modelMapper.map(updated, MemberDTO.class);
    }

    @Override
    public Optional<MemberDTO> getByMemberNo(Integer memberNo) {
        return memberRepository.findById(memberNo)
                .map(member -> modelMapper.map(member, MemberDTO.class));
    }

    @Override
    public void delete(Integer memberNo) {
        memberRepository.deleteById(memberNo);
    }

    // ✅ 추가 기능 구현
    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        MembershipType membershipType = membershipTypeRepository.findById(memberDTO.getMembershipTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Membership Type"));

        Member member = modelMapper.map(memberDTO, Member.class);
        member.setMembershipType(membershipType);

        Member saved = memberRepository.save(member);
        return modelMapper.map(saved, MemberDTO.class);
    }

    @Override
    public MemberDTO updateMember(Integer memberNo, MemberDTO memberDTO) {
        Member existing = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        modelMapper.map(memberDTO, existing);

        if (memberDTO.getMembershipTypeId() != null) {
            MembershipType membershipType = membershipTypeRepository.findById(memberDTO.getMembershipTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Membership Type"));
            existing.setMembershipType(membershipType);
        }

        Member updated = memberRepository.save(existing);
        return modelMapper.map(updated, MemberDTO.class);
    }

    @Override
    public boolean checkDuplicateLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Override
    public void changePassword(Integer memberNo, String newPassword) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        member.setPassword(newPassword);
        memberRepository.save(member);
    }

    @Override
    public void deactivateMember(Integer memberNo) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        member.setIsDeleted(true);
        memberRepository.save(member);
    }

    @Override
    public PageResponseDTO<MemberDTO> getPagedMembers(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable("memberNo");
        Page<Member> result = memberRepository.findAll(pageable);
        List<MemberDTO> dtoList = result.getContent().stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .toList();

        return PageResponseDTO.<MemberDTO>builder()
                .page(requestDTO.getPage())
                .size(requestDTO.getSize())
                .total((int) result.getTotalElements())
                .dtoList(dtoList)
                .build();
    }

    @Override
    public MemberDTO getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(member -> {
                    MemberDTO dto = modelMapper.map(member, MemberDTO.class);
                    if (member.getMembershipType() != null) {
                        dto.setMembershipTypeId(member.getMembershipType().getTypeId());
                        dto.setMembershipTypeName(member.getMembershipType().getTypeName());
                    }
                    return dto;
                })
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }
    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        // 영문자 + 숫자 + 특수문자 포함
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        if (!password.matches(pattern)) return false;

        // 같은 문자 3번 이상 반복 금지 (aaa, !!! 등)
        if (password.matches(".*(.)\\1\\1.*")) return false;

        return true;
    }

}
