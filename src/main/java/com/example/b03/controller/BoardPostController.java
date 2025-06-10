package com.example.b03.controller;

//import com.example.b03.domain.Member;
import com.example.b03.dto.*;
import com.example.b03.service.BoardCommentService;
import com.example.b03.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/boardpost")
@RequiredArgsConstructor
@Log4j2
public class BoardPostController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;


    // 게시글 목록
    @GetMapping("/list")
    public String list(BoardPageRequestDTO pageRequestDTO, Model model) {
        log.info("게시글 목록 요청: " + pageRequestDTO);

        BoardPageResponseDTO<BoardPostDTO> responseDTO;

        // 검색어(keyword)와 검색 타입(type)이 존재하는 경우 searchAll 서비스 호출
        // trim().isEmpty()를 사용하여 공백만 있는 경우도 처리
        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().trim().isEmpty()) {
            responseDTO = boardService.searchAll(pageRequestDTO);
        } else {
            // 검색어가 없는 경우 기존 getList 서비스 호출
            responseDTO = boardService.getList(pageRequestDTO);
        }

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO); // 검색 상태 유지를 위해 pageRequestDTO도 모델에 추가
        return "boardpost/list"; // Thymeleaf 템플릿 이름
    }

    // 게시글 작성 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("boardPostDTO", new BoardPostDTO());
        return "boardpost/register";
    }

    // 게시글 작성 처리
    @PostMapping("/register")
    public String registerPost(@ModelAttribute BoardPostDTO boardPostDTO, HttpSession session, Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("loginMember");

        if (memberDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 작성할 수 있습니다.");
            return "redirect:/member/login"; // 로그인 페이지로 리다이렉트하거나 오류 메시지 표시
        }

        try {
            // ✅ BoardService.register에 MemberDTO를 전달합니다.
            Integer postId = boardService.register(boardPostDTO, memberDTO);
            return "redirect:/boardpost/view/" + postId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("boardPostDTO", boardPostDTO);
            model.addAttribute("errorMessage", e.getMessage());
            return "boardpost/register"; // 오류 발생 시 작성 폼으로 돌아가기
        } catch (IllegalStateException e) {
            model.addAttribute("boardPostDTO", boardPostDTO);
            model.addAttribute("errorMessage", e.getMessage());
            return "boardpost/register"; // 오류 발생 시 작성 폼으로 돌아가기
        }
    }

    @GetMapping("/view/{postId}")
    public String view(@PathVariable("postId") Integer postId, Model model) {
        // 게시글 상세 조회
        BoardPostDTO dto = boardService.readOne(postId);

        // 댓글 목록 조회
        List<BoardCommentDTO> comments = boardCommentService.getCommentsByPostId(postId);

        // 모델에 게시글, 댓글, 새 댓글 입력용 DTO 추가
        model.addAttribute("dto", dto);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new BoardCommentDTO());

        return "boardpost/view";
    }

    // 게시글 수정 폼
    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable("postId") Integer postId, HttpSession session, Model model) { // ✅ HttpSession, Model 인자 추가
        MemberDTO currentUserDTO = (MemberDTO) session.getAttribute("loginMember");
        if (currentUserDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 수정할 수 있습니다.");
            return "redirect:/member/login";
        }

        BoardPostDTO dto = boardService.readOne(postId);

        // ✅ 수정 권한 확인: 게시글 작성자와 로그인한 사용자가 일치하는지 확인
        if (dto.getWriterName() != null && !dto.getWriterName().equals(currentUserDTO.getLoginId())) {
            model.addAttribute("errorMessage", "이 게시글을 수정할 권한이 없습니다.");
            return "redirect:/boardpost/view/" + postId; // 권한이 없으면 상세 페이지로 리다이렉트
        }

        model.addAttribute("boardPostDTO", dto);
        return "boardpost/edit";
    }


    // 게시글 수정 처리
    @PostMapping("/edit/{postId}")
    public String editPost(@PathVariable("postId") Integer postId,
                           @ModelAttribute BoardPostDTO boardPostDTO,
                           HttpSession session,
                           Model model) { // ✅ Model 인자 추가
        // ✅ 세션에서 MemberDTO를 가져옵니다.
        MemberDTO currentUserDTO = (MemberDTO) session.getAttribute("loginMember");
        if (currentUserDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 수정할 수 있습니다.");
            return "redirect:/member/login";
        }

        try {
            // ✅ BoardService.update에 MemberDTO를 전달합니다.
            boardService.update(postId, boardPostDTO, currentUserDTO);
            return "redirect:/boardpost/view/" + postId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("boardPostDTO", boardPostDTO); // 기존 데이터 유지를 위해 추가
            model.addAttribute("errorMessage", e.getMessage());
            return "boardpost/edit"; // 오류 발생 시 수정 폼으로 돌아가기
        } catch (IllegalStateException e) {
            model.addAttribute("boardPostDTO", boardPostDTO); // 기존 데이터 유지를 위해 추가
            model.addAttribute("errorMessage", e.getMessage());
            return "boardpost/edit"; // 오류 발생 시 수정 폼으로 돌아가기
        }
    }

    // 게시글 삭제
    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable("postId") Integer postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) { // ✅ RedirectAttributes 인자 추가
        // ✅ 세션에서 MemberDTO를 가져옵니다.
        MemberDTO currentUserDTO = (MemberDTO) session.getAttribute("loginMember");
        if (currentUserDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 게시글을 삭제할 수 있습니다.");
            return "redirect:/member/login";
        }

        try {
            // ✅ BoardService.delete에 MemberDTO를 전달합니다.
            boardService.delete(postId, currentUserDTO);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
            return "redirect:/boardpost/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/boardpost/view/" + postId; // 오류 발생 시 상세 페이지로 리다이렉트
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/boardpost/view/" + postId; // 오류 발생 시 상세 페이지로 리다이렉트
        }
    }

}

