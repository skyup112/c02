package com.example.b03.repository.search;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.QCompanyInfo;
import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompanyInfoSearchImpl implements CompanyInfoSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDTO<CompanyInfoDTO> search(PageRequestDTO requestDTO) {
        QCompanyInfo company = QCompanyInfo.companyInfo;

        Pageable pageable = requestDTO.getPageable("member.memberNo");

        BooleanBuilder builder = new BooleanBuilder();

        if (requestDTO.getTypes() != null && requestDTO.getKeyword() != null) {
            for (String type : requestDTO.getTypes()) {
                switch (type) {
                    case "n" -> builder.or(company.companyName.containsIgnoreCase(requestDTO.getKeyword()));
                    case "t" -> builder.or(company.techStack.containsIgnoreCase(requestDTO.getKeyword()));
                    case "d" -> builder.or(company.description.containsIgnoreCase(requestDTO.getKeyword()));
                }
            }
        }

        builder.and(company.member.memberNo.gt(0));

        List<CompanyInfo> resultList = queryFactory
                .selectFrom(company)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(company.member.memberNo.desc())
                .fetch();

        long totalCount = queryFactory
                .select(company.count())
                .from(company)
                .where(builder)
                .fetchOne();

        List<CompanyInfoDTO> dtoList = resultList.stream()
                .map(CompanyInfoDTO::fromEntity)
                .toList();

        return PageResponseDTO.<CompanyInfoDTO>withAll()
                .pageRequestDTO(requestDTO)
                .dtoList(dtoList)
                .total((int) totalCount)
                .build();
    }
}
