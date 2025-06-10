package com.example.b03.service;

import com.example.b03.domain.Member;
import com.example.b03.dto.BoardPageRequestDTO;
import com.example.b03.dto.BoardPageResponseDTO;
import com.example.b03.dto.BoardPostDTO;
import com.example.b03.dto.MemberDTO;

public interface BoardService {

    Integer register(BoardPostDTO boardPostDTO, MemberDTO memberDTO);

    BoardPostDTO readOne(Integer postId);

    Integer update(Integer postId, BoardPostDTO boardPostDTO, MemberDTO memberDTO);

    void delete(Integer postId, MemberDTO memberDTO);

    BoardPageResponseDTO<BoardPostDTO> getList(BoardPageRequestDTO boardPageRequestDTO);

    BoardPageResponseDTO<BoardPostDTO> searchAll(BoardPageRequestDTO boardPageRequestDTO);
}
