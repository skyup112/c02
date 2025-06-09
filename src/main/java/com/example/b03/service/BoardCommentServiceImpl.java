package com.example.b03.service;

import com.example.b03.domain.BoardComment;
import com.example.b03.domain.BoardPost;
import com.example.b03.domain.Member;
import com.example.b03.dto.BoardCommentDTO;
import com.example.b03.dto.BoardPageRequestDTO;
import com.example.b03.dto.BoardPageResponseDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.repository.BoardCommentRepository;
import com.example.b03.repository.BoardPostRepository;
import com.example.b03.repository.MemberRepository;
import com.example.b03.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardCommentServiceImpl implements BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardPostRepository boardPostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public Integer registerComment(BoardCommentDTO boardCommentDTO, MemberDTO memberDTO) {
        BoardPost boardPost = boardPostRepository.findById(boardCommentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Member member = memberRepository.findById(memberDTO.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        BoardComment boardComment = BoardComment.builder()
                .boardPost(boardPost)
                .member(member)
                .content(boardCommentDTO.getContent())
                .build();

        BoardComment savedBoardComment = boardCommentRepository.save(boardComment);
        log.info("댓글 등록 완료.commentId: {}", savedBoardComment.getCommentId());

        return savedBoardComment.getCommentId();
    }

    @Override
    public Integer updateComment(Integer commentId, BoardCommentDTO boardCommentDTO, MemberDTO memberDTO) {
        BoardComment existComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        Member currentUser = memberRepository.findById(memberDTO.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!existComment.getMember().getMemberNo().equals(currentUser.getMemberNo())) {
            throw new IllegalStateException("작성자만 수정 가능합니다.");
        }

        if (boardCommentDTO.getContent() == null || boardCommentDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글을 입력하세요.");
        }

        existComment.changeContent(boardCommentDTO.getContent());

        log.info("댓글이 수정되었습니다. commentId: {}", commentId);

        return commentId;
    }

    @Override
    public void deleteComment(Integer commentId, MemberDTO memberDTO) {
        BoardComment existComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        Member currentUser = memberRepository.findById(memberDTO.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!existComment.getMember().getMemberNo().equals(currentUser.getMemberNo())) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다. (작성자만 삭제 가능)");
        }

        boardCommentRepository.deleteById(commentId);
        log.info("댓글 삭제 완료. commentId: {}", commentId);
    }

    @Override
    public BoardPageResponseDTO<BoardCommentDTO> getListCommentsOfBoard(Integer postId, BoardPageRequestDTO boardPageRequestDTO) {
        boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Pageable pageable = boardPageRequestDTO.getPageable(Sort.by("createdAt").ascending());

        Page<BoardComment> result = boardCommentRepository.findByBoardPost_PostIdOrderByCreatedAtAsc(postId, pageable);

        List<BoardCommentDTO> dtoList = result.getContent().stream()
                .map(comment -> {
                    BoardCommentDTO dto = BoardCommentDTO.builder()
                            .commentId(comment.getCommentId())
                            .postId(comment.getBoardPost().getPostId())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .updatedAt(comment.getUpdatedAt())
                            .isDeleted(comment.getIsDeleted())
                            .build();

                    if (comment.getMember() != null) {
                        dto.setWriterName(comment.getMember().getLoginId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // --- BoardPageResponseDTO 빌더 수정 부분 ---
        int totalElements = (int) result.getTotalElements();
        int page = boardPageRequestDTO.getPage();
        int size = boardPageRequestDTO.getSize();

        // totalPage 계산 (BoardPageResponseDTO에는 없지만 계산은 필요)
        int totalPage = result.getTotalPages();

        // 페이지네이션 번호 계산 (10개씩 표시)
        int tempEnd = (int)(Math.ceil(page / 10.0)) * 10;
        int start = tempEnd - 9;
        int end = Math.min(totalPage, tempEnd); // 실제 마지막 페이지와 비교

        boolean prev = start > 1;
        boolean next = end < totalPage;

        List<Integer> pageNumList = IntStream.rangeClosed(start, end)
                .boxed()
                .collect(Collectors.toList());

        return BoardPageResponseDTO.<BoardCommentDTO>withAll()
                .dtoList(dtoList)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .start(start)      // 추가된 필드
                .end(end)          // 추가된 필드
                .prev(prev)        // 추가된 필드
                .next(next)        // 추가된 필드
                .pageNumList(pageNumList) // 추가된 필드
                .type(boardPageRequestDTO.getType()) // 검색 관련 필드
                .keyword(boardPageRequestDTO.getKeyword()) // 검색 관련 필드
                .build();
    }
    @Override
    public List<BoardCommentDTO> getCommentsByPostId(Integer postId) {
        boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<BoardComment> comments = boardCommentRepository.findByBoardPost_PostIdOrderByCreatedAtAsc(postId);

        return comments.stream().map(comment -> {
            BoardCommentDTO dto = BoardCommentDTO.builder()
                    .commentId(comment.getCommentId())
                    .postId(comment.getBoardPost().getPostId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .isDeleted(comment.getIsDeleted())
                    .build();

            if (comment.getMember() != null) {
                dto.setWriterName(comment.getMember().getLoginId());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}