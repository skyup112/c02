package com.example.b03.service;

import com.example.b03.domain.*;
import com.example.b03.dto.MemberDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.repository.*;
import com.example.b03.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO register(MemberDTO memberDTO) {
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());

        MembershipType membershipType = membershipTypeRepository.findById(memberDTO.getMembershipTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid membershipTypeId"));

        Member member = Member.builder()
                .loginId(memberDTO.getLoginId())
                .password(encodedPassword)
                .name(memberDTO.getName())
                .birthDate(memberDTO.getBirthDate())
                .address(memberDTO.getAddress())
                .phone(memberDTO.getPhone())
                .membershipType(membershipType)
                .build();

        return modelMapper.map(memberRepository.save(member), MemberDTO.class);
    }

    @Override
    public Optional<MemberDTO> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(member -> modelMapper.map(member, MemberDTO.class));
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

    @Override
    public MemberDTO update(MemberDTO dto) {
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setLoginId(dto.getLoginId());
        member.setName(dto.getName());
        member.setBirthDate(dto.getBirthDate());
        member.setAddress(dto.getAddress());
        member.setPhone(dto.getPhone());

        if (dto.getMembershipTypeId() != null) {
            MembershipType membershipType = membershipTypeRepository.findById(dto.getMembershipTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Membership Type"));
            member.setMembershipType(membershipType);
        }

        return modelMapper.map(memberRepository.save(member), MemberDTO.class);
    }

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

        return modelMapper.map(memberRepository.save(member), MemberDTO.class);
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

        return modelMapper.map(memberRepository.save(existing), MemberDTO.class);
    }

    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Override
    public void changePassword(Integer memberNo, String newPassword) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setPassword(passwordEncoder.encode(newPassword));
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
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        if (!password.matches(pattern)) return false;

        return !password.matches(".*(.)\\1\\1.*");
    }

    @Override
    public PageResponseDTO<MemberDTO> getPagedMembers(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable("memberNo");
        Page<Member> result = memberRepository.findAll(pageable);
        List<MemberDTO> dtoList = result.getContent().stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<MemberDTO>builder()
                .page(requestDTO.getPage())
                .size(requestDTO.getSize())
                .total((int) result.getTotalElements())
                .dtoList(dtoList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getLoginMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Integer memberNo = ((CustomUserDetails) principal).getMember().getMemberNo();

            Member member = memberRepository.findByIdWithMembershipType(memberNo)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            return MemberDTO.fromEntity(member);
        }

        String loginId = authentication.getName();
        Member member = memberRepository.findByLoginIdWithMembershipType(loginId).orElse(null);
        return member != null ? MemberDTO.fromEntity(member) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginIdWithMembershipType(loginId)
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

}
