package com.example.b03.controller;

import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
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
public class PostController {

    private final PostService postService;
    private final JobCategoryRepository jobCategoryRepository;
    private final PostJobCategoryService postJobCategoryService;

    // 공고 작성 폼
    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");
        if (member == null || member.getMembershipTypeId() != 2) {
            model.addAttribute("error", "기업회원만 이용 가능한 기능입니다.");
            return "redirect:/member/login";
        }

        model.addAttribute("post", new PostDTO());
        model.addAttribute("jobCategories", jobCategoryRepository.findAll()); // ✅ 직무 목록
        return "post/post-form";
    }

    // 공고 등록 처리
    @PostMapping("/create")
    public String createPost(@ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");
        if (member == null || member.getMembershipTypeId() != 2) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        dto.setCompanyMemberNo(member.getMemberNo());

        // 1. 공고 생성
        PostDTO savedPost = postService.createPost(dto);

        // 2. 직무 카테고리 연결
        postJobCategoryService.assignJobCategoriesToPost(savedPost.getPostId(), jobCategoryIds);

        redirectAttributes.addFlashAttribute("message", "공고가 등록되었습니다.");
        return "redirect:/member/mypage";
    }

    // ✅ 모든 공고 보기 + 직무 카테고리 필터링
    @GetMapping("/all")
    public String viewAllPosts(@RequestParam(value = "categoryId", required = false) String categoryIdStr,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {

        // page 음수 방지
        page = Math.max(page, 1);
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(10).build();

        // String to Integer 변환
        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isBlank()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                categoryId = null;
            }
        }

        PageResponseDTO<PostDTO> response = postService.search(pageRequestDTO, categoryId);

        model.addAttribute("result", response);
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        return "post/post-list-all";
    }


    // 공고 상세 보기
    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);

        // 선택된 직무 이름 리스트 주입 (뷰에서 사용)
        List<String> categoryNames = postJobCategoryService.getJobCategoriesByPost(id).stream()
                .map(pjc -> pjc.getJobCategory().getName())
                .toList();
        post.setJobCategoryNames(categoryNames);

        model.addAttribute("post", post);
        return "post/post-detail";
    }

    // 공고 수정 폼
    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);

        List<Integer> selectedCategoryIds = postJobCategoryService.getJobCategoriesByPost(id).stream()
                .map(pjc -> pjc.getJobCategory().getJobCategoryId())
                .toList();

        List<String> selectedCategoryNames = postJobCategoryService.getJobCategoriesByPost(id).stream()
                .map(pjc -> pjc.getJobCategory().getName())
                .toList();

        post.setJobCategoryNames(selectedCategoryNames);

        model.addAttribute("post", post);
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("selectedCategoryIds", selectedCategoryIds);
        return "post/post-edit";
    }

    // 공고 수정 처리
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Integer id,
                             @ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds,
                             RedirectAttributes redirectAttributes) {
        dto.setPostId(id);
        postService.updatePost(dto);

        postJobCategoryService.assignJobCategoriesToPost(id, jobCategoryIds);

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
