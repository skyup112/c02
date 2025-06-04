package com.example.b03.repository.search;

import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.dto.PostDTO;

public interface PostSearch {
    PageResponseDTO<PostDTO> search(PageRequestDTO requestDTO, Integer categoryId);
}
