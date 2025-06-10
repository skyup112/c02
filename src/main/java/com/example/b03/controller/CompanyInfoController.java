package com.example.b03.controller;

import com.example.b03.dto.*;
import com.example.b03.service.CompanyInfoService;
import com.example.b03.service.CompanyJobCategoryService;
import com.example.b03.service.JobCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;
    private final CompanyJobCategoryService companyJobCategoryService;
    private final JobCategoryService jobCategoryService;

    // 🔍 검색 포함 목록
    @GetMapping("/list")
    public String list(@ModelAttribute PageRequestDTO pageRequestDTO, Model model) {
        if (pageRequestDTO == null) {
            pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        }

        PageResponseDTO<CompanyInfoDTO> responseDTO = companyInfoService.search(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "company/list";
    }

    // 🔍 조회 / 수정 공용
    @GetMapping({"/read", "/modify"})
    public String readOrModify(@RequestParam("memberNo") Integer memberNo,
                               @ModelAttribute PageRequestDTO pageRequestDTO,
                               Model model) {
        try {
            CompanyInfoDTO dto = companyInfoService.getByMemberNo(memberNo);
            List<CompanyJobCategoryDTO> selectedJobCategories = companyJobCategoryService.getCategoriesByMemberNo(memberNo);
            List<JobCategoryDTO> allJobCategories = jobCategoryService.getAll();

            List<Integer> selectedIds = selectedJobCategories.stream()
                    .map(CompanyJobCategoryDTO::getJobCategoryId)
                    .collect(Collectors.toList());

            List<String> selectedNames = selectedJobCategories.stream()
                    .map(CompanyJobCategoryDTO::getJobCategoryName)
                    .collect(Collectors.toList());

            dto.setJobCategoryNames(selectedNames);

            model.addAttribute("dto", dto);
            model.addAttribute("jobCategories", allJobCategories);
            model.addAttribute("selectedJobCategoryIds", selectedIds);
            model.addAttribute("pageRequestDTO", pageRequestDTO);

        } catch (NoSuchElementException e) {
            log.error("❌ 회사 정보 조회 실패: {}", e.getMessage());
            return "redirect:/company/list";
        }

        return "company/" + (isModifyPage() ? "modify" : "read");
    }

    private boolean isModifyPage() {
        // 실제 요청 URL에 따라 판단하도록 개선 가능
        return true;
    }

    // ✏️ 수정 처리
    @PostMapping("/modify")
    public String modifySubmit(@ModelAttribute CompanyInfoDTO dto,
                               @RequestParam(value = "jobCategoryIds", required = false) List<Integer> jobCategoryIds,
                               @ModelAttribute PageRequestDTO pageRequestDTO) {

        log.info("✏️ Modify: memberNo={}, name={}", dto.getMemberNo(), dto.getCompanyName());

        companyInfoService.update(dto);

        if (jobCategoryIds == null) {
            jobCategoryIds = Collections.emptyList();
        }

        companyJobCategoryService.registerCategories(dto.getMemberNo(), jobCategoryIds);

        return "redirect:/member/mypage";
    }

    // 🗑️ 삭제
    @PostMapping("/remove")
    public String remove(@RequestParam("memberNo") Integer memberNo,
                         @ModelAttribute PageRequestDTO pageRequestDTO) {
        try {
            companyInfoService.delete(memberNo);
            log.info("🗑️ 삭제 완료: memberNo={}", memberNo);
        } catch (NoSuchElementException e) {
            log.warn("⚠️ 삭제 실패: {}", e.getMessage());
        }

        return "redirect:/company/list?page=" + pageRequestDTO.getPage()
                + "&size=" + pageRequestDTO.getSize()
                + "&type=" + (pageRequestDTO.getType() == null ? "" : pageRequestDTO.getType())
                + "&keyword=" + (pageRequestDTO.getKeyword() == null ? "" : pageRequestDTO.getKeyword());
    }

    // 🆕 등록 폼
    @GetMapping("/register")
    public String registerForm(@RequestParam("memberNo") Integer memberNo, Model model) {
        List<JobCategoryDTO> jobCategories = jobCategoryService.getAll();
        model.addAttribute("jobCategories", jobCategories);
        model.addAttribute("memberNo", memberNo);
        return "company/register";
    }

    // ✅ 등록 처리
    @PostMapping("/register")
    public String registerSubmit(@RequestParam("memberNo") Integer memberNo,
                                 @ModelAttribute CompanyInfoDTO dto,
                                 @RequestParam(value = "jobCategoryIds", required = false) List<Integer> jobCategoryIds) {

        dto.setMemberNo(memberNo); // 혹시 모르니 명시적으로 지정
        log.info("🆕 등록 요청: memberNo={}, 회사명={}", memberNo, dto.getCompanyName());

        CompanyInfoDTO savedDTO = companyInfoService.register(dto);

        if (jobCategoryIds != null && !jobCategoryIds.isEmpty()) {
            companyJobCategoryService.registerCategories(savedDTO.getMemberNo(), jobCategoryIds);
        }

        return "redirect:/member/mypage";
    }
}



