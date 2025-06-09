
package com.example.b03.controller;

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
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardPostController {

    private final BoardService boardService;
    private final BoardCommentService boardCommentService;

    // 🔹 게시글 목록
    @GetMapping("/list")
    public String list(BoardPageRequestDTO pageRequestDTO, Model model) {
        BoardPageResponseDTO<BoardPostDTO> responseDTO = boardService.getList(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "board/list";
    }

    // 🔹 게시글 작성 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("boardPostDTO", new BoardPostDTO());
        return "board/register";
    }

    // 🔹 게시글 작성 처리
    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute BoardPostDTO boardPostDTO,
                                 HttpSession session,
                                 Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("loginMember");

        if (memberDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 작성할 수 있습니다.");
            return "redirect:/member/login";
        }

        try {
            Integer postId = boardService.register(boardPostDTO, memberDTO);
            return "redirect:/board/view/" + postId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("boardPostDTO", boardPostDTO);
            model.addAttribute("errorMessage", e.getMessage());
            return "board/register";
        }
    }

    // 🔹 게시글 상세 보기
    @GetMapping("/view/{postId}")
    public String view(@PathVariable("postId") Integer postId, Model model) {
        BoardPostDTO dto = boardService.readOne(postId);
        List<BoardCommentDTO> comments = boardCommentService.getCommentsByPostId(postId);

        model.addAttribute("dto", dto);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new BoardCommentDTO());

        return "board/view";
    }

    // 🔹 게시글 수정 폼
    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable("postId") Integer postId,
                           HttpSession session,
                           Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("loginMember");

        if (memberDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 수정할 수 있습니다.");
            return "redirect:/member/login";
        }

        BoardPostDTO dto = boardService.readOne(postId);

        if (dto.getWriterName() != null && !dto.getWriterName().equals(memberDTO.getLoginId())) {
            model.addAttribute("errorMessage", "이 게시글을 수정할 권한이 없습니다.");
            return "redirect:/board/view/" + postId;
        }

        model.addAttribute("boardPostDTO", dto);
        return "board/edit";
    }

    // 🔹 게시글 수정 처리
    @PostMapping("/edit/{postId}")
    public String editSubmit(@PathVariable("postId") Integer postId,
                             @ModelAttribute BoardPostDTO boardPostDTO,
                             HttpSession session,
                             Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("loginMember");

        if (memberDTO == null) {
            model.addAttribute("errorMessage", "로그인 후 게시글을 수정할 수 있습니다.");
            return "redirect:/member/login";
        }

        try {
            boardService.update(postId, boardPostDTO, memberDTO);
            return "redirect:/board/view/" + postId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("boardPostDTO", boardPostDTO);
            model.addAttribute("errorMessage", e.getMessage());
            return "board/edit";
        }
    }

    // 🔹 게시글 삭제 처리
    @PostMapping("/delete/{postId}")
    public String delete(@PathVariable("postId") Integer postId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("loginMember");

        if (memberDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 게시글을 삭제할 수 있습니다.");
            return "redirect:/member/login";
        }

        try {
            boardService.delete(postId, memberDTO);
            redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/board/list";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/view/" + postId;
        }
    }

}


