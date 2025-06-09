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

//import static com.example.b03.domain.QMember.member;

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

        // 게시글 작성자의 로그인 ID를 writerName으로 보여주고 싶은 경우
        dto.setWriterName(boardPost.getMember().getLoginId());

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

        boardPost.changeTitle(boardPostDTO.getTitle()); // setter 대신 비즈니스 로직 메서드 사용 권장
        boardPost.changeContent(boardPostDTO.getContent()); // setter 대신 비즈니스 로직 메서드 사용 권장

        // BaseEntity에 updateTime이 있다면 자동으로 갱신됩니다.
        // boardPostRepository.save(boardPost); // @Transactional이 붙어있어 변경 감지로 자동 저장됩니다.

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
        // boardPageRequestDTO에서 페이징 정보 (페이지 번호, 사이즈, 정렬)를 추출
        // 기본 정렬은 postId 내림차순
        Pageable pageable = boardPageRequestDTO.getPageable(Sort.by("postId").descending());

        // 검색 타입과 키워드 추출
        String type = boardPageRequestDTO.getType();
        String keyword = boardPageRequestDTO.getKeyword();

        // BoardSearchRepository의 searchAll 메소드를 사용하여 게시글 목록과 페이징 정보를 가져옴
        Page<BoardPost> result = boardPostRepository.searchAll(type, keyword, pageable);

        // Page<BoardPost>를 List<BoardPostDTO>로 변환
        List<BoardPostDTO> dtoList = result.getContent().stream()
                .map(boardPost -> {
                    BoardPostDTO dto = modelMapper.map(boardPost, BoardPostDTO.class);
                    // ModelMapper 설정에 member.loginId -> writerName 매핑을 추가했다면 이 줄은 제거 가능
                    if (boardPost.getMember() != null) { // member가 null인 경우 방지
                        dto.setWriterName(boardPost.getMember().getLoginId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // BoardPageResponseDTO를 빌더 패턴으로 생성하여 반환
        return BoardPageResponseDTO.<BoardPostDTO>withAll()
                .dtoList(dtoList)
                .totalElements((int) result.getTotalElements())
                .page(boardPageRequestDTO.getPage())
                .size(boardPageRequestDTO.getSize())
                .type(type)
                .keyword(keyword)
                .build();
    }

}
