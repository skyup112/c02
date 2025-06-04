package com.example.b03.service;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Member;
import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.repository.CompanyInfoRepository;
import com.example.b03.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;
    private final MemberRepository memberRepository;
//    @Autowired
//    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CompanyInfoDTO register(CompanyInfoDTO dto) {
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        if (member.getMembershipType() == null || member.getMembershipType().getTypeId() != 3) {
            throw new IllegalArgumentException("해당 회원은 기업회원이 아닙니다.");
        }


        CompanyInfo companyInfo = CompanyInfo.builder()
                .member(member)
                .companyName(dto.getCompanyName())
                .foundedDate(dto.getFoundedDate())
                .employeeCount(dto.getEmployeeCount())
                .revenue(dto.getRevenue())
                .techStack(dto.getTechStack())
                .homepageUrl(dto.getHomepageUrl())
                .description(dto.getDescription())
                .build();

        CompanyInfo saved = companyInfoRepository.save(companyInfo);
        return CompanyInfoDTO.fromEntity(saved);
    }

//    public CompanyInfoDTO register(CompanyInfoDTO dto) {
//        Member member = memberRepository.findById(dto.getMemberNo())
//                .orElseThrow(() -> new NoSuchElementException("Member not found"));
//
//        if (member.getMembershipType() == null || member.getMembershipType().getTypeId() != 2) {
//            throw new IllegalArgumentException("해당 회원은 기업회원이 아닙니다.");
//        }
//
//
//        CompanyInfo companyInfo = modelMapper.map(dto, CompanyInfo.class);
//        companyInfo.setMember(member);
//
//        CompanyInfo saved = companyInfoRepository.save(companyInfo);
//
//        return CompanyInfoDTO.fromEntity(saved);
//    }


    @Override
    public CompanyInfoDTO getByMemberNo(Integer memberNo) {
        CompanyInfo companyInfo = companyInfoRepository.findByMember_MemberNo(memberNo)
                .orElseThrow(() -> new NoSuchElementException("CompanyInfo not found"));

        return CompanyInfoDTO.fromEntity(companyInfo);
    }

//
//    public CompanyInfoDTO getByMemberNo(Integer memberNo) {
//        CompanyInfo companyInfo = companyInfoRepository.findByMember_MemberNo(memberNo)
//                .orElseThrow(() -> new NoSuchElementException("CompanyInfo not found"));
//
//        return modelMapper.map(companyInfo, CompanyInfoDTO.class);
//    }




    @Override
    @Transactional
    public CompanyInfoDTO update(CompanyInfoDTO dto) {
        CompanyInfo existing = companyInfoRepository.findByMember_MemberNo(dto.getMemberNo())
                .orElseThrow(() -> new NoSuchElementException("CompanyInfo not found"));

        // 기업회원 여부 확인
        if (existing.getMember().getMembershipType() == null ||
                existing.getMember().getMembershipType().getTypeId() != 3) {
            throw new IllegalArgumentException("해당 회원은 기업회원(MembershipType 3번)이 아닙니다.");
        }

        existing.setCompanyName(dto.getCompanyName());
        existing.setFoundedDate(dto.getFoundedDate());
        existing.setEmployeeCount(dto.getEmployeeCount());
        existing.setRevenue(dto.getRevenue());
        existing.setTechStack(dto.getTechStack());
        existing.setHomepageUrl(dto.getHomepageUrl());
        existing.setDescription(dto.getDescription());

        //  Member 안의 address, phone 수정
        existing.getMember().setPhone(dto.getPhone());
        existing.getMember().setAddress(dto.getAddress());

        return CompanyInfoDTO.fromEntity(existing);
    }

//    @Override
//    @Transactional
//    public CompanyInfoDTO update(CompanyInfoDTO dto) {
//        CompanyInfo existing = companyInfoRepository.findByMember_MemberNo(dto.getMemberNo())
//                .orElseThrow(() -> new NoSuchElementException("CompanyInfo not found"));
//
//        if (existing.getMember().getMembershipType() == null ||
//                existing.getMember().getMembershipType().getTypeId() != 2) {
//            throw new IllegalArgumentException("해당 회원은 기업회원(MembershipType 2번)이 아닙니다.");
//        }
//
//        modelMapper.map(dto, existing);
//
//        existing.getMember().setPhone(dto.getPhone());
//        existing.getMember().setAddress(dto.getAddress());
//
//        return modelMapper.map(existing, CompanyInfoDTO.class);
//    }


    @Override
    @Transactional
    public void delete(Integer memberNo) {
        CompanyInfo companyInfo = companyInfoRepository.findByMember_MemberNo(memberNo)
                .orElseThrow(() -> new NoSuchElementException("CompanyInfo not found"));

        //        2번타입 기업회원과 1번 관리자 타입만 삭제가능
        Byte typeId = companyInfo.getMember().getMembershipType() != null
                ? companyInfo.getMember().getMembershipType().getTypeId()
                : null;

        if (typeId == null || !(typeId == 1 || typeId == 3)) {
            throw new IllegalArgumentException("삭제는 기업회원(2) 또는 관리자(1)만 가능합니다.");
        }
        companyInfoRepository.delete(companyInfo);
    }



    @Override
    public PageResponseDTO<CompanyInfoDTO> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable("memberNo");
        Page<CompanyInfo> result = companyInfoRepository.findAll(pageable);

        List<CompanyInfoDTO> dtoList = result.getContent()
                .stream()
                .map(CompanyInfoDTO::fromEntity) //  ModelMapper 대신 정적 메서드 사용
                .toList();

        return PageResponseDTO.<CompanyInfoDTO>withAll()
                .pageRequestDTO(requestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

//    @Override
//    public PageResponseDTO<CompanyInfoDTO> getList(PageRequestDTO requestDTO) {
//        Pageable pageable = requestDTO.getPageable("memberNo");
//        Page<CompanyInfo> result = companyInfoRepository.findAll(pageable);
//
//        List<CompanyInfoDTO> dtoList = result.getContent()
//                .stream()
//                .map(entity -> modelMapper.map(entity, CompanyInfoDTO.class))
//                .toList();
//
//        return PageResponseDTO.<CompanyInfoDTO>withAll()
//                .pageRequestDTO(requestDTO)
//                .dtoList(dtoList)
//                .total((int) result.getTotalElements())
//                .build();
//    }

}




