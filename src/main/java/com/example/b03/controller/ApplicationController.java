package com.example.b03.controller;

import com.example.b03.dto.ApplicationDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // 기업회원만 다운로드 허용
    @GetMapping("/download/{applicationId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Integer applicationId,
                                                   HttpSession session) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null || loginMember.getMembershipTypeId() != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ApplicationDTO application = applicationService.getById(applicationId);
        Path filePath = Paths.get(application.getFilePath());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 지원서 작성 폼 이동
    @GetMapping("/form")
    public String showApplicationForm(@RequestParam("postId") Integer postId,
                                      @RequestParam("memberNo") Integer memberNo,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (applicationService.isDuplicateApplication(postId, memberNo)) {
            redirectAttributes.addFlashAttribute("error", "이미 해당 공고에 지원하셨습니다.");
            return "redirect:/member/mypage";
        }

        model.addAttribute("postId", postId);
        model.addAttribute("memberNo", memberNo);
        return "/project/application/application_form";
    }

    // 지원서 제출
    @PostMapping("/apply")
    public String applyToPost(@ModelAttribute ApplicationDTO applicationDTO,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null || loginMember.getMembershipTypeId() != 2) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/member/login";
        }

        try {
            applicationService.applyToPost(applicationDTO, file);
            redirectAttributes.addFlashAttribute("message", "지원이 완료되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/member/mypage";
    }

    // 기업: 특정 공고의 지원자 목록
    @GetMapping("/company/{companyMemberNo}/post/{postId}/applicants")
    public String getApplicantsByPost(@PathVariable Integer companyMemberNo,
                                      @PathVariable Integer postId,
                                      Model model) {
        List<Map<String, Object>> applications = applicationService
                .getApplicationsWithMemberInfoByPostAndCompany(postId, companyMemberNo);

        model.addAttribute("applications", applications);
        model.addAttribute("companyMemberNo", companyMemberNo);
        model.addAttribute("postId", postId);

        return "application/applications_by_company";
    }

    // 개인: 내가 지원한 내역 보기 (필요 시)
    @GetMapping("/member/{memberNo}")
    public String getApplicationsByMember(@PathVariable Integer memberNo, Model model) {
        List<ApplicationDTO> applications = applicationService.getApplicationsByMember(memberNo);
        model.addAttribute("applications", applications);
        return "application/applications_by_member";
    }
}
