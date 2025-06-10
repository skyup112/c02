// 3. CompanyInfo
package com.example.b03.repository;

import com.example.b03.domain.CompanyInfo;
import com.example.b03.domain.Member;
import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.repository.search.CompanyInfoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Integer>, CompanyInfoSearch {
    Optional<CompanyInfo> findByMember_MemberNo(Integer memberNo);
//        PageResponseDTO<CompanyInfoDTO> search(PageRequestDTO requestDTO);


}
