package com.example.b03.controller;

import com.example.b03.dto.PostDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.repository.JobCategoryRepository;
import com.example.b03.service.PostJobCategoryService;
import com.example.b03.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostViewController {

    private final PostService postService;
    private final JobCategoryRepository jobCategoryRepository;
    private final PostJobCategoryService postJobCategoryService;

    // 공고 작성 폼
    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");
        if (member == null || member.getMembershipTypeId() != 3) {
            model.addAttribute("error", "기업회원만 이용 가능한 기능입니다.");
            return "redirect:/member/login";
        }

        model.addAttribute("post", new PostDTO());
        model.addAttribute("jobCategories", jobCategoryRepository.findAll()); // ✅ 직무 목록
        return "project/post/post-form";
    }

    // 공고 등록 처리
    @PostMapping("/create")
    public String createPost(@ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds, // ← 추가
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");
        if (member == null || member.getMembershipTypeId() != 3) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        dto.setCompanyMemberNo(member.getMemberNo());

        // 1. 공고 생성
        PostDTO savedPost = postService.createPost(dto);

        // 2. 직무 카테고리 연결
        postJobCategoryService.assignJobCategoriesToPost(savedPost.getPostId(), jobCategoryIds); // ← 이 부분 추가

        redirectAttributes.addFlashAttribute("message", "공고가 등록되었습니다.");
        return "redirect:/member/mypage";
    }

    @GetMapping("/all")
    public String viewAllPosts(Model model) {
        List<PostDTO> posts = postService.getAllPosts(); // 모든 공고를 가져오는 서비스 메서드
        model.addAttribute("posts", posts); // 뷰에 공고 목록 전달
        return "project/post/post-list-all"; // 모든 공고를 보여주는 뷰로 이동
    }

    // 공고 상세 보기 (기업회원용)
    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "project/post/post-detail";
    }

    // 공고 수정 폼
    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);
        List<Integer> selectedCategoryIds = postJobCategoryService.getJobCategoriesByPost(id)
                .stream()
                .map(pjc -> pjc.getJobCategory().getJobCategoryId())
                .toList();

        model.addAttribute("post", post);
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("selectedCategoryIds", selectedCategoryIds);
        return "project/post/post-edit";
    }

    // 공고 수정 처리
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Integer id,
                             @ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds, // ← 추가
                             RedirectAttributes redirectAttributes) {
        dto.setPostId(id);
        postService.updatePost(dto);

        // 기존 직무카테고리 삭제 후 새로 등록
        postJobCategoryService.assignJobCategoriesToPost(id, jobCategoryIds); // ← 추가

        redirectAttributes.addFlashAttribute("message", "공고가 수정되었습니다.");
        return "redirect:/post/view/" + id;
    }

    // 공고 삭제
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("message", "공고가 삭제되었습니다.");
        return "redirect:/member/mypage";
    }

}
