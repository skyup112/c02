package com.example.b03.controller;

import com.example.b03.dto.MemberDTO;
import com.example.b03.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class LoginViewController {

    private final MemberService memberService;

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
        memberService.createMember(dto);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/member/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String loginId,
                               @RequestParam String password,
                               Model model,
                               HttpSession session) {
        if (loginId == null || loginId.isBlank()) {
            model.addAttribute("error", "아이디를 입력해주세요.");
            return "project/login";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("error", "비밀번호를 입력해주세요.");
            return "project/login";
        }

        MemberDTO member;
        try {
            member = memberService.getMemberByLoginId(loginId);
        } catch (Exception e) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "project/login";
        }

        if (!password.equals(member.getPassword())) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "project/login";
        }

        session.setAttribute("loginMember", member); // ✅ 반드시 DTO를 세션에 저장
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

    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", loginMember);

        if (loginMember.getMembershipTypeId() == 2) {
            return "project/mypage/edit-user";
        } else if (loginMember.getMembershipTypeId() == 3) {
            return "project/mypage/edit-company";
        } else {
            return "redirect:/member/mypage";
        }
    }

    @PostMapping("/update")
    public String updateMember(MemberDTO dto, HttpSession session) {
        MemberDTO updated = memberService.update(dto); // 회원 정보 수정
        session.setAttribute("loginMember", updated);  // 세션 최신화
        return "redirect:/member/mypage";              // 마이페이지로 리디렉션
    }

}
