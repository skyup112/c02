package com.example.b03.repository.search;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.QCompanyInfo;
import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyInfoSearchImpl extends QuerydslRepositorySupport implements CompanyInfoSearch {

    public CompanyInfoSearchImpl() {
        super(CompanyInfo.class);
    }

    @Override
    public PageResponseDTO<CompanyInfoDTO> search(PageRequestDTO requestDTO) {
        QCompanyInfo company = QCompanyInfo.companyInfo;
        Pageable pageable = requestDTO.getPageable("member.memberNo");

        JPQLQuery<CompanyInfo> query = from(company);

        if (requestDTO.getTypes() != null && requestDTO.getKeyword() != null) {
            String keyword = requestDTO.getKeyword();
            BooleanBuilder builder = new BooleanBuilder();

            for (String type : requestDTO.getTypes()) {
                switch (type) {
                    case "n" -> builder.or(company.companyName.containsIgnoreCase(keyword));
                    case "t" -> builder.or(company.techStack.containsIgnoreCase(keyword));
                    case "d" -> builder.or(company.description.containsIgnoreCase(keyword));
                }
            }

            query.where(builder);
        }

        query.where(company.member.memberNo.gt(0));
        getQuerydsl().applyPagination(pageable, query);

        List<CompanyInfo> resultList = query.fetch();
        long totalCount = query.fetchCount();

        List<CompanyInfoDTO> dtoList = resultList.stream()
                .map(CompanyInfoDTO::fromEntity)
                .collect(Collectors.toList());

        return PageResponseDTO.<CompanyInfoDTO>withAll()
                .pageRequestDTO(requestDTO)
                .dtoList(dtoList)
                .total((int) totalCount)
                .build();
    }
}
