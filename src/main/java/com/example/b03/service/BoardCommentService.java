package com.example.b03.service;

import com.example.b03.domain.Member;
import com.example.b03.dto.*;

import java.util.List;

public interface BoardCommentService {

    Integer registerComment(BoardCommentDTO boardCommentDTO, MemberDTO memberDTO);

    Integer updateComment(Integer commentId, BoardCommentDTO boardCommentDTO, MemberDTO memberDTO);

    void deleteComment(Integer commentId, MemberDTO memberDTO);

    // 게시글 내 댓글의 목록
    BoardPageResponseDTO<BoardCommentDTO> getListCommentsOfBoard(Integer postId, BoardPageRequestDTO boardPageRequestDTO);

    List<BoardCommentDTO> getCommentsByPostId(Integer postId);

}
