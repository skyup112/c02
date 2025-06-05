package com.example.b03.controller;

import com.example.b03.domain.JobCategory;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.dto.PostDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.repository.JobCategoryRepository;
import com.example.b03.service.PostJobCategoryService;
import com.example.b03.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;
    private final JobCategoryRepository jobCategoryRepository;
    private final PostJobCategoryService postJobCategoryService;

    // 공고 작성 폼
    @GetMapping("/register")
    public String showCreateForm(HttpSession session, Model model) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");
        if (member == null || member.getMembershipTypeId() != 2) {
            model.addAttribute("error", "기업회원만 이용 가능한 기능입니다.");
            return "redirect:/member/login";
        }

        model.addAttribute("post", new PostDTO());
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        return "post/register";
    }

    // 공고 등록 처리
    @PostMapping("/register")
    public String createPost(@ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        MemberDTO member = (MemberDTO) session.getAttribute("loginMember");

        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (member.getMembershipTypeId() != 2) {
            redirectAttributes.addFlashAttribute("error", "기업회원만 공고 등록이 가능합니다.");
            return "redirect:/member/main";
        }

        if (jobCategoryIds == null || jobCategoryIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "직무 카테고리를 하나 이상 선택해주세요.");
            return "redirect:/post/register";
        }

        dto.setCompanyMemberNo(member.getMemberNo());

        try {
            PostDTO savedPost = postService.createPost(dto);
            postJobCategoryService.assignJobCategoriesToPost(savedPost.getPostId(), jobCategoryIds);
            redirectAttributes.addFlashAttribute("message", "공고가 등록되었습니다.");
            return "redirect:/member/mypage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "공고 등록 중 알 수 없는 오류가 발생했습니다.");
        }

        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("post", dto);
        return "post/register";
    }

    // 모든 공고 보기 + 직무 카테고리 필터링
    @GetMapping("/list")
    public String viewAllPosts(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(Math.max(page, 1))
                .size(10)
                .build();

        PageResponseDTO<PostDTO> response = postService.search(pageRequestDTO, categoryId);

        model.addAttribute("result", response);
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        return "post/list";
    }

    // 공고 상세 보기
    @GetMapping("/read/{id}")
    public String viewPost(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);

        List<String> categoryNames = postJobCategoryService.getCategoriesByPostId(id).stream()
                .map(JobCategory::getName)
                .toList();
        post.setJobCategoryNames(categoryNames);

        model.addAttribute("post", post);
        return "post/read";
    }

    // 공고 수정 폼
    @GetMapping("/modify/{id}")
    public String editPostForm(@PathVariable Integer id, Model model) {
        PostDTO post = postService.getPostById(id);

        List<JobCategory> selectedCategories = postJobCategoryService.getCategoriesByPostId(id);
        List<Integer> selectedCategoryIds = selectedCategories.stream()
                .map(JobCategory::getJobCategoryId)
                .toList();
        List<String> selectedCategoryNames = selectedCategories.stream()
                .map(JobCategory::getName)
                .toList();

        post.setJobCategoryNames(selectedCategoryNames);

        model.addAttribute("post", post);
        model.addAttribute("jobCategories", jobCategoryRepository.findAll());
        model.addAttribute("selectedCategoryIds", selectedCategoryIds);
        return "post/modify";
    }

    // 공고 수정 처리
    @PostMapping("/modify/{id}")
    public String updatePost(@PathVariable Integer id,
                             @ModelAttribute PostDTO dto,
                             @RequestParam List<Integer> jobCategoryIds,
                             RedirectAttributes redirectAttributes) {
        dto.setPostId(id);
        postService.updatePost(dto);
        postJobCategoryService.assignJobCategoriesToPost(id, jobCategoryIds);

        log.info("location from form: " + dto.getLocation());

        redirectAttributes.addFlashAttribute("message", "공고가 수정되었습니다.");
        return "redirect:/post/read/" + id;
    }

    // 공고 삭제
    @PostMapping("/remove/{id}")
    public String deletePost(@PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("message", "공고가 삭제되었습니다.");
        return "redirect:/member/mypage";
    }
}
