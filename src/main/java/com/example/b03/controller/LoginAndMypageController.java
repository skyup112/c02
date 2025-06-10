package com.example.b03.controller;

import com.example.b03.domain.Member;
import com.example.b03.dto.*;
import com.example.b03.repository.MemberRepository;
import com.example.b03.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class LoginAndMypageController {

    private final MemberService memberService;
    private final ApplicationService applicationService;
    private final PostService postService;
    private final CompanyInfoService companyInfoService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 로그인된 멤버 정보 반환 (없으면 null)
    private MemberDTO getLoginMember() {
        return memberService.getLoginMember();
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "message", required = false) String message,
                            Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember != null) {
            return "redirect:/member/main";
        }

        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }

        return "main/login";
    }

    @GetMapping("/join")
    public String selectJoinType() {
        return "main/join";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam("type") int type, Model model) {
        String memberTypeName = (type == 3) ? "개인" : "기업";
        model.addAttribute("membershipTypeId", (byte) type);
        model.addAttribute("memberTypeName", memberTypeName);
        return "main/register";
    }

    @GetMapping("/check-loginid")
    @ResponseBody
    public Map<String, Boolean> checkLoginId(@RequestParam String loginId) {
        boolean exists = memberService.isLoginIdDuplicate(loginId);
        return Collections.singletonMap("exists", exists);
    }

    @PostMapping("/register")
    public String submitRegister(MemberDTO dto, RedirectAttributes redirectAttributes) {
        if (memberService.isLoginIdDuplicate(dto.getLoginId())) {
            redirectAttributes.addFlashAttribute("message", "이미 사용중인 아이디입니다.");
            return "redirect:/member/register?type=" + dto.getMembershipTypeId();
        }

        if (!memberService.isValidPassword(dto.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "비밀번호는 8자 이상, 영문+숫자+특수문자 포함, 같은 문자 반복 불가입니다.");
            return "redirect:/member/register?type=" + dto.getMembershipTypeId();
        }

        memberService.register(dto); // 서비스에서 암호화 처리
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/member/login";
    }

    @GetMapping("/mypage")
    public String mypage(@RequestParam(defaultValue = "1") int memberPage,
                         @RequestParam(defaultValue = "1") int postPage,
                         @RequestParam(defaultValue = "members") String section,
                         Model model) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null || Boolean.TRUE.equals(loginMember.getIsDeleted())) {
            model.addAttribute("error", "로그인 후 이용 가능합니다.");
            return "mypage/mypage-guest";
        }

        model.addAttribute("member", loginMember);
        model.addAttribute("section", section);

        Byte typeId = loginMember.getMembershipTypeId();
        if (typeId == null) {
            model.addAttribute("error", "회원 유형 정보가 없습니다.");
            return "mypage/mypage-guest";
        }

        switch (typeId) {
            case 1 -> { // 관리자
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
            case 3 -> { // 개인 회원
                List<ApplicationDTO> applications = applicationService.getApplicationsByMember(loginMember.getMemberNo());
                model.addAttribute("applications", applications);
                return "mypage/mypage-user";
            }
            case 2 -> { // 기업 회원
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

                    memberService.getByMemberNo(app.getMemberNo()).ifPresent(applicant -> {
                        map.put("applicantName", applicant.getName());
                        map.put("applicantPhone", applicant.getPhone());
                    });

                    return map;
                }).collect(Collectors.toList());

                model.addAttribute("applicants", applicantInfos);
                return "mypage/mypage-company";
            }
            default -> {
                return "mypage/mypage-guest";
            }
        }
    }

    @PostMapping("/admin/member/remove/{memberNo}")
    public String deactivateMember(@PathVariable Integer memberNo) {
        memberService.deactivateMember(memberNo);
        return "redirect:/member/mypage";
    }

    @PostMapping("/admin/post/remove/{postId}")
    public String deletePost(@PathVariable Integer postId) {
        postService.deletePost(postId);
        return "redirect:/member/mypage";
    }

    @GetMapping("/applicants/{postId}")
    public String viewApplicantsForPost(@PathVariable Integer postId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = getLoginMember();

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

    @GetMapping("/main")
    public String mainPage(Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember != null) {
            model.addAttribute("name", loginMember.getName());
        }
        return "main/main";
    }

    @GetMapping("/modify")
    public String showEditForm(Model model) {
        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", loginMember);

        int membershipTypeId = loginMember.getMembershipTypeId();

        if (membershipTypeId == 2) {
            return "mypage/edit-company";
        } else {
            return "mypage/edit-user";
        }
    }

    @PostMapping("/modify")
    public String updateMemberInfo(@ModelAttribute MemberDTO dto,
                                   @RequestParam String confirmPassword,
                                   RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = getLoginMember();

        if (loginMember == null || !passwordEncoder.matches(confirmPassword, loginMember.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/mypage";
        }

        dto.setMemberNo(loginMember.getMemberNo());
        dto.setLoginId(loginMember.getLoginId());
        dto.setPassword(loginMember.getPassword());
        dto.setMembershipTypeId(loginMember.getMembershipTypeId());

        memberService.update(dto);
        redirectAttributes.addFlashAttribute("message", "회원정보가 수정되었습니다.");
        return "redirect:/member/mypage";
    }

    @GetMapping("/password-change")
    public String passwordChangeForm(Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", loginMember);
        return "mypage/password-change";
    }

    @PostMapping("/remove")
    public String deleteMember(@RequestParam String confirmPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 상태가 아닙니다.");
            return "redirect:/member/mypage";
        }

        // 실제 DB에서 멤버 정보를 가져와야 함 (비밀번호 비교를 위해)
        Member member = memberRepository.findById(loginMember.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(confirmPassword, member.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/mypage";
        }

        memberService.deactivateMember(loginMember.getMemberNo());
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/member/login";
    }

    @PostMapping("/password-change")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) throws ServletException {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        if (!passwordEncoder.matches(currentPassword, loginMember.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/member/password-change";
        }

        if (!memberService.isValidPassword(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호는 8자 이상, 영문+숫자+특수문자 포함, 같은 문자 반복 불가입니다.");
            return "redirect:/member/password-change";
        }

        memberService.changePassword(loginMember.getMemberNo(), newPassword);
        request.logout();

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되어 로그아웃 되었습니다. 다시 로그인 해주세요.");
        return "redirect:/member/login";
    }
}
