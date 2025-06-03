package com.example.b03.controller;

import com.example.b03.domain.Member;
import com.example.b03.dto.ApplicationDTO;
import com.example.b03.dto.MemberDTO;
import com.example.b03.dto.PostDTO;
import com.example.b03.service.ApplicationService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class LoginViewController {

    private final MemberService memberService;
    private final ApplicationService applicationService;
    private final PostService postService;

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

        // 유효성 검사 추가
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
        if (loginId == null || loginId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "아이디를 입력해주세요.");
            return "redirect:/member/login";
        }

        if (password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "비밀번호를 입력해주세요.");
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
        Object loginMemberObj = session.getAttribute("loginMember");

        if (loginMemberObj == null) {
            model.addAttribute("error", "로그인 후 이용 가능합니다.");
            return "project/mypage/mypage-guest";
        }

        MemberDTO loginMember = (MemberDTO) loginMemberObj;
        model.addAttribute("member", loginMember);

        Byte typeId = loginMember.getMembershipTypeId();
        if (typeId == null) {
            model.addAttribute("error", "회원 유형 정보가 없습니다.");
            return "project/mypage/mypage-guest";
        }

        if (typeId == 2) {
            List<ApplicationDTO> applications = applicationService.getApplicationsByMember(loginMember.getMemberNo());
            model.addAttribute("applications", applications);
        }

        if (typeId == 3) {
            List<PostDTO> posts = postService.getPostsByCompany(loginMember.getMemberNo());
            model.addAttribute("posts", posts);
        }

        if (loginMember.getMembershipTypeId() == 3) {
            // 전체 공고에 대한 지원서 조회
            List<ApplicationDTO> applications = applicationService.getApplicationsByCompany(loginMember.getMemberNo());

            // DTO → Map 으로 바꾸고, 지원자 이름/전화번호 포함시킬 수 있다면 따로 확장
            List<Map<String, Object>> applicantInfos = applications.stream().map(app -> {
                Map<String, Object> map = new HashMap<>();
                map.put("applicationId", app.getApplicationId());
                map.put("postId", app.getPostId());
                map.put("postTitle", app.getPostTitle());
                map.put("filePath", app.getFilePath());
                map.put("submittedAt", app.getSubmittedAt());

                // 🔽 이름/전화번호도 포함하려면 추가 정보가 필요함 (ApplicationDTO 확장 필요)
                MemberDTO member = memberService.getByMemberNo(app.getMemberNo()).orElse(null);
                if (member != null) {
                    map.put("applicantName", member.getName());
                    map.put("applicantPhone", member.getPhone());
                }

                return map;
            }).collect(Collectors.toList());

            model.addAttribute("applicants", applicantInfos);
        }

        switch (typeId) {
            case 1:
                return "project/mypage/mypage-admin";
            case 2:
                return "project/mypage/mypage-user";
            case 3:
                return "project/mypage/mypage-company";
            default:
                model.addAttribute("error", "알 수 없는 회원 유형입니다.");
                return "project/mypage/mypage-guest";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 로그인 세션 제거
        return "redirect:/member/main"; // 로그인 페이지로 이동
    }

    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }

        return "project/main"; // => src/main/resources/templates/member/main.html
    }

    // GET: 수정 페이지 보여주기
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
    public String updateMemberInfo(
            @ModelAttribute MemberDTO dto,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        // ✅ 비밀번호 확인
        if (!loginMember.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/edit";
        }

        dto.setMemberNo(loginMember.getMemberNo());
        dto.setLoginId(loginMember.getLoginId());
        dto.setPassword(loginMember.getPassword());
        dto.setMembershipTypeId(loginMember.getMembershipTypeId());

        memberService.update(dto);

        // 최신 정보로 세션 갱신
        MemberDTO refreshed = memberService.getMemberByLoginId(loginMember.getLoginId());
        session.setAttribute("loginMember", refreshed);

        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/member/mypage";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam String confirmPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (!loginMember.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/mypage";
        }

        memberService.deactivateMember(loginMember.getMemberNo());
        session.invalidate();  // 로그아웃

        redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/member/login";
    }

    @GetMapping("/password-change")
    public String passwordChangeForm() {
        return "project/mypage/password-change";  // view 경로 맞춰주세요
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

        // ✅ 유효성 검사
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
