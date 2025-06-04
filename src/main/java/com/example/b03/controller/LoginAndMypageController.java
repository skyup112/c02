package com.example.b03.controller;

import com.example.b03.dto.ApplicationDTO;
import com.example.b03.dto.CompanyInfoDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.dto.PostDTO;
import com.example.b03.service.ApplicationService;
import com.example.b03.service.CompanyInfoService;
import com.example.b03.service.MemberService;
import com.example.b03.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class LoginAndMypageController {

    private final MemberService memberService;
    private final ApplicationService applicationService;
    private final PostService postService;
    private final CompanyInfoService companyInfoService;

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("message") String message, Model model) {
        if (!message.isEmpty()) model.addAttribute("message", message);
        return "project/login";
    }

    @GetMapping("/join")
    public String selectJoinType() {
        return "project/join";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam("type") int type, Model model) {
        String memberTypeName = (type == 2) ? "개인" : "기업";
        model.addAttribute("membershipTypeId", (byte) type);
        model.addAttribute("memberTypeName", memberTypeName);
        return "project/register";
    }

    @PostMapping("/register")
    public String submitRegister(MemberDTO dto, RedirectAttributes redirectAttributes) {
        if (!memberService.isValidPassword(dto.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "비밀번호는 8자 이상, 영문+숫자+특수문자 포함, 같은 문자 반복 불가입니다.");
            return "redirect:/member/register?type=" + dto.getMembershipTypeId();
        }

        memberService.createMember(dto);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/member/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String loginId,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (loginId.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "아이디와 비밀번호를 모두 입력해주세요.");
            return "redirect:/member/login";
        }

        MemberDTO member;
        try {
            member = memberService.getMemberByLoginId(loginId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/login";
        }

        if (!password.equals(member.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/login";
        }

        if (Boolean.TRUE.equals(member.getIsDeleted())) {
            redirectAttributes.addFlashAttribute("error", "이미 탈퇴한 계정입니다.");
            return "redirect:/member/login";
        }

        session.setAttribute("loginMember", member);
        return "redirect:/member/main";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            model.addAttribute("error", "로그인 후 이용 가능합니다.");
            return "project/mypage/mypage-guest";
        }

        model.addAttribute("member", loginMember);

        Byte typeId = loginMember.getMembershipTypeId();
        if (typeId == null) {
            model.addAttribute("error", "회원 유형 정보가 없습니다.");
            return "project/mypage/mypage-guest";
        }

        if (typeId == 1) {
            // 관리자
            return "project/mypage/mypage-admin";
        }

        if (typeId == 2) {
            // 개인 회원
            List<ApplicationDTO> applications = applicationService.getApplicationsByMember(loginMember.getMemberNo());
            model.addAttribute("applications", applications);
            return "project/mypage/mypage-user";
        }

        if (typeId == 3) {
            // 기업 회원
            try {
                CompanyInfoDTO companyInfo = companyInfoService.getByMemberNo(loginMember.getMemberNo());
                model.addAttribute("companyInfo", companyInfo);
            } catch (NoSuchElementException e) {
                // 🔴 회사 정보가 없는 경우 등록 페이지로 이동
                return "redirect:/member/company/edit";
            }

            List<PostDTO> posts = postService.getPostsByCompany(loginMember.getMemberNo());
            model.addAttribute("posts", posts);

            List<ApplicationDTO> applications = applicationService.getApplicationsByCompany(loginMember.getMemberNo());
            List<Map<String, Object>> applicantInfos = applications.stream().map(app -> {
                Map<String, Object> map = new HashMap<>();
                map.put("applicationId", app.getApplicationId());
                map.put("postId", app.getPostId());
                map.put("postTitle", app.getPostTitle());
                map.put("filePath", app.getFilePath());
                map.put("submittedAt", app.getSubmittedAt());
                MemberDTO member = memberService.getByMemberNo(app.getMemberNo()).orElse(null);
                if (member != null) {
                    map.put("applicantName", member.getName());
                    map.put("applicantPhone", member.getPhone());
                }
                return map;
            }).toList();
            model.addAttribute("applicants", applicantInfos);

            return "project/mypage/mypage-company";
        }


        return "project/mypage/mypage-guest"; // fallback
    }

    @GetMapping("/applicants/{postId}")
    public String viewApplicantsForPost(@PathVariable Integer postId,
                                        HttpSession session,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null || loginMember.getMembershipTypeId() != 3) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/member/login";
        }

        PostDTO post = postService.getPostById(postId);
        if (post == null || !post.getMemberNo().equals(loginMember.getMemberNo())) {
            redirectAttributes.addFlashAttribute("error", "해당 공고에 접근할 수 없습니다.");
            return "redirect:/member/mypage";
        }

        List<Map<String, Object>> applicantInfos = applicationService
                .getApplicationsWithMemberInfoByPostAndCompany(postId, loginMember.getMemberNo());

        model.addAttribute("post", post);
        model.addAttribute("applicants", applicantInfos);
        return "project/mypage/applicants-for-post";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/main";
    }

    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }
        return "project/main";
    }

    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("member", loginMember);
        return "project/mypage/edit-user";
    }

    @PostMapping("/edit")
    public String updateMemberInfo(@ModelAttribute MemberDTO dto,
                                   @RequestParam String confirmPassword,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (!loginMember.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/edit";
        }

        dto.setMemberNo(loginMember.getMemberNo());
        dto.setLoginId(loginMember.getLoginId());
        dto.setPassword(loginMember.getPassword());
        dto.setMembershipTypeId(loginMember.getMembershipTypeId());

        memberService.update(dto);
        session.setAttribute("loginMember", memberService.getMemberByLoginId(loginMember.getLoginId()));

        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/member/mypage";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam String confirmPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null || !loginMember.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않거나 로그인되지 않았습니다.");
            return "redirect:/member/mypage";
        }

        memberService.deactivateMember(loginMember.getMemberNo());
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/member/login";
    }

    @GetMapping("/password-change")
    public String passwordChangeForm() {
        return "project/mypage/password-change";
    }

    @PostMapping("/password-change")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (!currentPassword.equals(loginMember.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/password-change";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/password-change";
        }

        if (!memberService.isValidPassword(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호는 8자 이상, 영문+숫자+특수문자 포함, 같은 문자 반복 불가입니다.");
            return "redirect:/member/password-change";
        }

        memberService.changePassword(loginMember.getMemberNo(), newPassword);
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 다시 로그인 해주세요.");
        return "redirect:/member/login";
    }
}
