package com.example.b03.service;

import com.example.b03.domain.Inquiry;
import com.example.b03.domain.Member; // Member 엔티티 임포트 추가
import com.example.b03.repository.MemberRepository; // MemberRepository 임포트 추가
import com.example.b03.dto.InquiryListDTO;
import com.example.b03.dto.InquiryPageRequestDTO;
import com.example.b03.dto.InquiryPageResponseDTO;
import com.example.b03.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository; // ⭐ 추가: MemberRepository 주입
    private final ModelMapper modelMapper;

    @Override
    public Integer register(InquiryListDTO inquiryListDTO) {
        // ⭐ 수정: memberNo로 Member 엔티티 조회 후 Inquiry에 설정
        Member member = memberRepository.findById(inquiryListDTO.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다: " + inquiryListDTO.getMemberNo()));

        Inquiry inquiry = modelMapper.map(inquiryListDTO, Inquiry.class);
        inquiry.setMember(member); // ⭐ 추가: 조회한 Member 엔티티를 Inquiry에 설정

        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        log.info("새로운 문의 등록: " + savedInquiry.getInquiryId());
        return savedInquiry.getInquiryId();
    }

    @Override
    public InquiryListDTO readOne(Integer inquiryId) {
        Optional<Inquiry> result = inquiryRepository.findById(inquiryId);
        Inquiry inquiry = result.orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다: " + inquiryId));

        if (inquiry.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 문의글입니다.");
        }

        return modelMapper.map(inquiry, InquiryListDTO.class);
    }

    @Override
    public void modify(InquiryListDTO inquiryListDTO) {
        Optional<Inquiry> result = inquiryRepository.findById(inquiryListDTO.getInquiryId());
        Inquiry inquiry = result.orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다: " + inquiryListDTO.getInquiryId()));

        // ⭐ 추가: 작성자 검증 로직
        if (!inquiry.getMember().getMemberNo().equals(inquiryListDTO.getMemberNo())) {
            throw new IllegalArgumentException("문의 작성자만 수정할 수 있습니다.");
        }

        if (inquiry.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 문의글은 수정할 수 없습니다.");
        }

        inquiry.setTitle(inquiryListDTO.getTitle());
        inquiry.setContent(inquiryListDTO.getContent());

        // inquiryRepository.save(inquiry); // 더티 체킹에 의해 자동 저장되므로 이 줄은 생략 가능
        log.info("문의 수정 완료: " + inquiry.getInquiryId());
    }

    @Override
    public void remove(Integer inquiryId) {
        Optional<Inquiry> result = inquiryRepository.findById(inquiryId);
        Inquiry inquiry = result.orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다: " + inquiryId));

        if (inquiry.getIsDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 문의글입니다.");
        }

        // ⭐ 논리적 삭제: isDeleted 필드를 true로 설정
        inquiry.setIsDeleted(true);

        // 변경된 엔티티를 저장하여 DB에 반영 (더티 체킹에 의해 자동 반영되므로 생략 가능하나 명시적으로 호출해도 무방)
        inquiryRepository.save(inquiry);

        log.info("문의 삭제 완료 (논리적 삭제): " + inquiryId);
    }

    @Override
    public InquiryPageResponseDTO<InquiryListDTO> list(InquiryPageRequestDTO inquiryPageRequestDTO) {
        Pageable pageable = inquiryPageRequestDTO.getPageable();

        Page<Inquiry> result;

        if (inquiryPageRequestDTO.getSearchType() != null && inquiryPageRequestDTO.getSearchKeyword() != null
                && !inquiryPageRequestDTO.getSearchType().trim().isEmpty() && !inquiryPageRequestDTO.getSearchKeyword().trim().isEmpty()) {
            result = inquiryRepository.searchAll(
                    new String[]{inquiryPageRequestDTO.getSearchType()},
                    inquiryPageRequestDTO.getSearchKeyword(),
                    pageable
            );
            log.info("검색 결과: " + result.getTotalElements() + "개");
        } else {
            result = inquiryRepository.findByIsDeletedFalseOrderByInquiryIdDesc(pageable);
            log.info("전체 목록 (검색 조건 없음): " + result.getTotalElements() + "개");
        }

        List<InquiryListDTO> dtoList = result.getContent().stream()
                .map(inquiry -> modelMapper.map(inquiry, InquiryListDTO.class))
                .collect(Collectors.toList());

        return InquiryPageResponseDTO.<InquiryListDTO>withAll()
                .dtoList(dtoList)
                .totalCount((int) result.getTotalElements())
                .page(inquiryPageRequestDTO.getPage())
                .size(inquiryPageRequestDTO.getSize())
                .build();
    }
}