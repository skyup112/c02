package com.example.b03.controller;

import com.example.b03.dto.ApplicationDTO;
import com.example.b03.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    public String applyToPost(@RequestParam("postId") Integer postId,
                              @RequestParam("memberNo") Integer memberNo,
                              @RequestParam("resume") MultipartFile resume,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // 로그인 확인
        Object loginMember = session.getAttribute("loginMember");
        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // 중복 지원 체크
        if (applicationService.isDuplicateApplication(postId, memberNo)) {
            redirectAttributes.addFlashAttribute("error", "이미 해당 공고에 지원했습니다.");
            return "redirect:/member/mypage";
        }

        // DTO 생성 및 서비스 호출
        ApplicationDTO dto = ApplicationDTO.builder()
                .postId(postId)
                .memberNo(memberNo)
                .build();

        try {
            applicationService.applyToPost(dto, resume);
            redirectAttributes.addFlashAttribute("message", "지원이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "지원 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/member/mypage";
    }
}
