package com.example.b03.controller;

import com.example.b03.dto.MemberDTO;
import com.example.b03.service.CompanyInfoService;
import com.example.b03.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberNo}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Integer memberNo) {
        return memberService.getByMemberNo(memberNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/login-id/{loginId}")
    public ResponseEntity<MemberDTO> getMemberByLoginId(@PathVariable String loginId) {
        return ResponseEntity.ok(memberService.getMemberByLoginId(loginId));
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO dto) {
        return ResponseEntity.ok(memberService.createMember(dto));
    }

    @PutMapping("/{memberNo}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Integer memberNo, @RequestBody MemberDTO dto) {
        return ResponseEntity.ok(memberService.updateMember(memberNo, dto));
    }

    @DeleteMapping("/{memberNo}")
    public ResponseEntity<Void> delete(@PathVariable Integer memberNo) {
        memberService.delete(memberNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-id/{loginId}")
    public ResponseEntity<Boolean> checkDuplicateLoginId(@PathVariable String loginId) {
        return ResponseEntity.ok(memberService.checkDuplicateLoginId(loginId));
    }
    @PatchMapping("/{memberNo}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer memberNo,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        memberService.changePassword(memberNo, newPassword);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{memberNo}/deactivate")
    public ResponseEntity<?> deactivateMember(@PathVariable Integer memberNo) {
        memberService.deactivateMember(memberNo);
        return ResponseEntity.ok().build();
    }

}
