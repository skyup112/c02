package com.example.b03.controller;

import com.example.b03.domain.MembershipType;
import com.example.b03.dto.*;
import com.example.b03.service.*;
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
    private final MembershipTypeService membershipTypeservice;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(@ModelAttribute("message") String message, Model model) {
        if (!message.isEmpty()) model.addAttribute("message", message);
        return "main/login";
    }

    // 회원가입 유형 선택
    @GetMapping("/join")
    public String selectJoinType() {
        return "main/join";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage(@RequestParam("type") int type, Model model) {
        String memberTypeName = (type == 3) ? "개인" : "기업";
        model.addAttribute("membershipTypeId", (byte) type);
        model.addAttribute("memberTypeName", memberTypeName);
        return "main/register";
    }

    // 회원가입 처리
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

    // 로그인 처리
    @PostMapping("/login")
    public String processLogin(@RequestParam String loginId,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        if (loginId.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "아이디와 비밀번호를 모두 입력해주세요.");
            return "redirect:/member/login";
        }

        try {
            MemberDTO member = memberService.getMemberByLoginId(loginId);

            if (!password.equals(member.getPassword()) || Boolean.TRUE.equals(member.getIsDeleted())) {
                redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않거나 탈퇴한 계정입니다.");
                return "redirect:/member/login";
            }

            // ✅ 로그인 성공 시 세션에 저장
            session.setAttribute("loginMember", member);
            return "redirect:/member/main";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/login";
        }
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@RequestParam(defaultValue = "1") int memberPage,
                         @RequestParam(defaultValue = "1") int postPage,
                         @RequestParam(defaultValue = "members") String section,  // 추가
                         Model model,
                         HttpSession session) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            model.addAttribute("error", "로그인 후 이용 가능합니다.");
            return "mypage/mypage-guest";
        }

        model.addAttribute("member", loginMember);
        model.addAttribute("section", section); // 선택적으로 뷰에 넘겨도 됨

        Byte typeId = loginMember.getMembershipTypeId();
        if (typeId == null) {
            model.addAttribute("error", "회원 유형 정보가 없습니다.");
            return "mypage/mypage-guest";
        }

        if (typeId == 1) {
            PageRequestDTO memberPageRequest = PageRequestDTO.builder().page(memberPage).size(10).build();
            PageRequestDTO postPageRequest = PageRequestDTO.builder().page(postPage).size(10).build();

            PageResponseDTO<MemberDTO> memberResult = memberService.getPagedMembers(memberPageRequest);
            PageResponseDTO<PostDTO> postResult = postService.getPagedPosts(postPageRequest);

            AdminStatsDTO stats = AdminStatsDTO.builder()
                    .totalMembers(memberResult.getTotal())
                    .totalPosts(postResult.getTotal())
                    .totalApplications(applicationService.getAllApplications().size())
                    .build();

            model.addAttribute("memberResult", memberResult);
            model.addAttribute("postResult", postResult);
            model.addAttribute("stats", stats);
            return "mypage/mypage-admin";
        }

        if (typeId == 3) {
            List<ApplicationDTO> applications = applicationService.getApplicationsByMember(loginMember.getMemberNo());
            model.addAttribute("applications", applications);
            return "mypage/mypage-user";
        }

        if (typeId == 2) {
            try {
                CompanyInfoDTO companyInfo = companyInfoService.getByMemberNo(loginMember.getMemberNo());
                model.addAttribute("companyInfo", companyInfo);
            } catch (NoSuchElementException e) {
                CompanyInfoDTO emptyInfo = new CompanyInfoDTO();
                emptyInfo.setMemberNo(loginMember.getMemberNo());
                model.addAttribute("companyInfo", emptyInfo);
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

                MemberDTO applicant = memberService.getByMemberNo(app.getMemberNo()).orElse(null);
                if (applicant != null) {
                    map.put("applicantName", applicant.getName());
                    map.put("applicantPhone", applicant.getPhone());
                }
                return map;
            }).collect(Collectors.toList());

            model.addAttribute("applicants", applicantInfos);
            return "mypage/mypage-company";
        }

        return "mypage/mypage-guest";
    }

    // 관리자: 회원 탈퇴
    @PostMapping("/admin/member/remove/{memberNo}")
    public String deactivateMember(@PathVariable Integer memberNo) {
        memberService.deactivateMember(memberNo);
        return "redirect:/member/mypage";
    }

    // 관리자: 공고 삭제
    @PostMapping("/admin/post/remove/{postId}")
    public String deletePost(@PathVariable Integer postId) {
        postService.deletePost(postId);
        return "redirect:/member/mypage";
    }

    // 기업회원: 지원자 조회
    @GetMapping("/applicants/{postId}")
    public String viewApplicantsForPost(@PathVariable Integer postId,
                                        HttpSession session,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null || loginMember.getMembershipTypeId() != 2) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/member/login";
        }

        PostDTO post = postService.getPostById(postId);
        if (post == null || !post.getCompanyMemberNo().equals(loginMember.getMemberNo())) {
            redirectAttributes.addFlashAttribute("error", "해당 공고에 접근할 수 없습니다.");
            return "redirect:/member/mypage";
        }

        List<Map<String, Object>> applicants = applicationService.getApplicationsWithMemberInfoByPostAndCompany(postId, loginMember.getMemberNo());
        model.addAttribute("post", post);
        model.addAttribute("applicants", applicants);
        return "mypage/applicants-for-post";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/main";
    }

    // 메인 페이지
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }
        return "main/main";
    }

    // 회원정보 수정
    @GetMapping("/modify")
    public String showEditForm(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", loginMember);

        int membershipTypeId = loginMember.getMembershipTypeId(); // 멤버십 타입 ID 가져오기

        if (membershipTypeId == 2) {       // 기업회원이면
            return "mypage/edit-company";
        } else if (membershipTypeId == 3) { // 개인회원이면
            return "mypage/edit-user";
        } else {
            // 혹시 모를 기본 경로 (관리자 등)
            return "mypage/edit-user";
        }
    }

    @PostMapping("/modify")
    public String updateMemberInfo(@ModelAttribute MemberDTO dto,
                                   @RequestParam String confirmPassword,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null || !loginMember.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/mypage";
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

    // 회원 탈퇴
    @PostMapping("/remove")
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

    // 비밀번호 변경
    @GetMapping("/password-change")
    public String passwordChangeForm() {
        return "mypage/password-change";
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
