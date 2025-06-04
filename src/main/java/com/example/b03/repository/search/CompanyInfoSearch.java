package com.example.b03.repository.search;

import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;

public interface CompanyInfoSearch {
    PageResponseDTO<CompanyInfoDTO> search(PageRequestDTO pageRequestDTO);
}
