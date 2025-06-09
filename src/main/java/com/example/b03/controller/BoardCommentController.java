package com.example.b03.controller;

import com.example.b03.dto.BoardCommentDTO;
import com.example.b03.dto.BoardPageRequestDTO;
import com.example.b03.dto.BoardPageResponseDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.service.BoardCommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Log4j2
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    // 댓글 등록 (Ajax 방식)
    @PostMapping("")
    public ResponseEntity<?> register(@RequestBody @Valid BoardCommentDTO dto,
                                      HttpSession session) {
        // 세션에서 MemberDTO를 가져옵니다. ClassCastException이 발생하지 않도록 MemberDTO로 직접 캐스팅합니다.
        MemberDTO loginMemberDTO = (MemberDTO) session.getAttribute("loginMember");
        if (loginMemberDTO == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 서비스 메서드에 MemberDTO 객체를 전달합니다.
        Integer commentId = boardCommentService.registerComment(dto, loginMemberDTO);
        return ResponseEntity.ok(commentId);
    }

    // 댓글 수정 (Ajax 방식)
    @PutMapping("/{commentId}")
    public ResponseEntity<?> update(@PathVariable Integer commentId,
                                    @RequestBody @Valid BoardCommentDTO dto,
                                    HttpSession session) {
        // 세션에서 MemberDTO를 가져옵니다.
        MemberDTO loginMemberDTO = (MemberDTO) session.getAttribute("loginMember");
        if (loginMemberDTO == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 서비스 메서드에 MemberDTO 객체를 전달합니다.
        boardCommentService.updateComment(commentId, dto, loginMemberDTO);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    // 댓글 삭제 (Ajax 방식)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Integer commentId,
                                    HttpSession session) {
        // 세션에서 MemberDTO를 가져옵니다.
        MemberDTO loginMemberDTO = (MemberDTO) session.getAttribute("loginMember");
        if (loginMemberDTO == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 서비스 메서드에 MemberDTO 객체를 전달합니다.
        boardCommentService.deleteComment(commentId, loginMemberDTO);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // 댓글 목록 조회는 변경 없음 (이 메서드는 MemberDTO를 사용하지 않으므로 변경 불필요)
    @GetMapping("/post/{postId}")
    public ResponseEntity<BoardPageResponseDTO<BoardCommentDTO>> getList(@PathVariable Integer postId,
                                                                         @ModelAttribute BoardPageRequestDTO requestDTO) {
        BoardPageResponseDTO<BoardCommentDTO> responseDTO = boardCommentService.getListCommentsOfBoard(postId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}