package com.example.b03.service;

import com.example.b03.domain.BoardPost;
import com.example.b03.domain.Member;
import com.example.b03.dto.BoardPageRequestDTO;
import com.example.b03.dto.BoardPageResponseDTO;
import com.example.b03.dto.BoardPostDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.repository.BoardPostRepository;
import com.example.b03.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream; // IntStream 임포트 추가

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardPostRepository boardPostRepository;
    private final MemberRepository memberRepository;

    @Override
    public Integer register(BoardPostDTO boardPostDTO, MemberDTO memberDTO) {
        if (memberDTO == null) {
            throw new IllegalStateException("로그인된 사용자만 게시글을 등록할 수 있습니다.");
        }
        if (boardPostDTO.getTitle() == null || boardPostDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력하세요.");
        }
        if (boardPostDTO.getContent() == null || boardPostDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용을 입력하세요.");
        }

        Member authorMember = memberRepository.findById(memberDTO.getMemberNo())
                .orElseThrow(() -> new IllegalStateException("게시글 작성자를 찾을 수 없습니다. (memberNo: " + memberDTO.getMemberNo() + ")"));

        BoardPost post = modelMapper.map(boardPostDTO, BoardPost.class);
        post.setMember(authorMember);

        Integer postId = boardPostRepository.save(post).getPostId();

        if (postId == null) {
            throw new IllegalStateException("게시글 등록에 실패했습니다.");
        }
        return postId;
    }

    @Override
    public BoardPostDTO readOne(Integer postId) {
        BoardPost boardPost = boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        BoardPostDTO dto = modelMapper.map(boardPost, BoardPostDTO.class);
        if (boardPost.getMember() != null) {
            dto.setWriterName(boardPost.getMember().getLoginId());
        } else {
            dto.setWriterName("Unknown"); // 작성자 정보가 없는 경우 처리
        }
        return dto;
    }

    @Override
    public Integer update(Integer postId, BoardPostDTO boardPostDTO, MemberDTO memberDTO) {
        Optional<BoardPost> result = boardPostRepository.findById(postId);
        BoardPost boardPost = result.orElseThrow(() ->
                new IllegalArgumentException("수정하려는 게시글이 존재하지 않습니다."));

        if (!boardPost.getMember().getMemberNo().equals(memberDTO.getMemberNo())) {
            throw new IllegalStateException("게시글을 수정할 권한이 없습니다. (작성자만 수정 가능)");
        }
        if (boardPostDTO.getTitle() == null || boardPostDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력하세요.");
        }
        if (boardPostDTO.getContent() == null || boardPostDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용을 입력하세요.");
        }

        boardPost.changeTitle(boardPostDTO.getTitle());
        boardPost.changeContent(boardPostDTO.getContent());

        log.info("게시글 수정 완료. postId: {}", postId);
        return postId;
    }

    @Override
    public void delete(Integer postId, MemberDTO memberDTO) {
        Optional<BoardPost> result = boardPostRepository.findById(postId);
        BoardPost boardPost = result.orElseThrow(() ->
                new IllegalArgumentException("삭제하려는 게시글이 존재하지 않습니다."));

        if (!boardPost.getMember().getMemberNo().equals(memberDTO.getMemberNo())) {
            throw new IllegalStateException("게시글을 삭제할 권한이 없습니다. (작성자만 삭제 가능)");
        }

        boardPostRepository.deleteById(postId);
        log.info("게시글 삭제 완료. postId: {}", postId);
    }

    @Override
    public BoardPageResponseDTO<BoardPostDTO> getList(BoardPageRequestDTO boardPageRequestDTO) {
        Pageable pageable = boardPageRequestDTO.getPageable(Sort.by("postId").descending());

        String type = boardPageRequestDTO.getType();
        String keyword = boardPageRequestDTO.getKeyword();

        Page<BoardPost> result = boardPostRepository.searchAll(type, keyword, pageable);

        List<BoardPostDTO> dtoList = result.getContent().stream()
                .map(boardPost -> {
                    BoardPostDTO dto = modelMapper.map(boardPost, BoardPostDTO.class);
                    if (boardPost.getMember() != null) {
                        dto.setWriterName(boardPost.getMember().getLoginId());
                    } else {
                        dto.setWriterName("Unknown");
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // ⭐ BoardPageResponseDTO의 새로운 생성자를 사용하여 페이징 정보 자동 계산
        return new BoardPageResponseDTO<>(result, dtoList, boardPageRequestDTO);
    }

    @Override
    public BoardPageResponseDTO<BoardPostDTO> searchAll(BoardPageRequestDTO boardPageRequestDTO) {
        Pageable pageable = boardPageRequestDTO.getPageable(Sort.by("postId").descending());
        String type = boardPageRequestDTO.getType();
        String keyword = boardPageRequestDTO.getKeyword();

        // BoardSearchRepositoryImpl의 searchAll 메서드 호출
        Page<BoardPost> result = boardPostRepository.searchAll(type, keyword, pageable);

        List<BoardPostDTO> dtoList = result.getContent().stream()
                .map(boardPost -> {
                    BoardPostDTO dto = modelMapper.map(boardPost, BoardPostDTO.class);
                    if (boardPost.getMember() != null) {
                        dto.setWriterName(boardPost.getMember().getLoginId());
                    } else {
                        dto.setWriterName("Unknown");
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new BoardPageResponseDTO<>(result, dtoList, boardPageRequestDTO);
    }
}