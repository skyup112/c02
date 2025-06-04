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
    private final JobCategoryService jobCategoryService; //  전체 직무 카테고리 조회용

    @GetMapping("/list")
    public void list(Model model, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<CompanyInfoDTO> responseDTO = companyInfoService.getList(pageRequestDTO);
        log.info("CompanyInfo list: {}", responseDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping({"/read", "/modify"})
    public void readOrModify(@RequestParam("memberNo") Integer memberNo,
                             @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                             Model model) {
        CompanyInfoDTO dto = companyInfoService.getByMemberNo(memberNo);

        List<CompanyJobCategoryDTO> selectedJobCategories = companyJobCategoryService.getCategoriesByMemberNo(memberNo);
        List<JobCategoryDTO> allJobCategories = jobCategoryService.getAll(); // 전체 목록

        // 🔹 선택된 카테고리 ID 목록 (modify 체크박스용)
        List<Integer> selectedJobCategoryIds = selectedJobCategories.stream()
                .map(CompanyJobCategoryDTO::getJobCategoryId)
                .collect(Collectors.toList());

        // 🔹 선택된 카테고리 이름 목록 (read 화면 표시용)
        List<String> selectedJobCategoryNames = selectedJobCategories.stream()
                .map(CompanyJobCategoryDTO::getJobCategoryName)
                .collect(Collectors.toList());

        dto.setJobCategoryNames(selectedJobCategoryNames); // 🔥 DTO에 주입

        model.addAttribute("dto", dto);
        model.addAttribute("jobCategories", allJobCategories); // modify용
        model.addAttribute("selectedJobCategoryIds", selectedJobCategoryIds); // modify용
        model.addAttribute("pageRequestDTO", pageRequestDTO);
    }


    // 수정 처리
    @PostMapping("/modify")
    public String modifySubmit(CompanyInfoDTO dto,
                               @RequestParam(value = "jobCategoryIds", required = false) List<Integer> jobCategoryIds,
                               PageRequestDTO pageRequestDTO) {

        log.info("Modify Submit: {}", dto);

        // 회사 정보 업데이트
        companyInfoService.update(dto);

        // 체크박스 선택이 없을 경우를 대비하여 빈 리스트 처리
        if (jobCategoryIds == null) {
            jobCategoryIds = List.of(); // 또는 new ArrayList<>()
        }

        // 직무 카테고리 수정 반영
        companyJobCategoryService.registerCategories(dto.getMemberNo(), jobCategoryIds);



        return "redirect:/member/mypage";
    }


    // 삭제 처리
    @PostMapping("/remove")
    public String remove(@RequestParam("memberNo") Integer memberNo, PageRequestDTO pageRequestDTO) {
        log.info("Remove memberNo: {}", memberNo);
        companyInfoService.delete(memberNo);
        return "redirect:/company/list?page=" + pageRequestDTO.getPage()
                + "&size=" + pageRequestDTO.getSize();
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam("memberNo") Integer memberNo, Model model) {
        List<JobCategoryDTO> jobCategories = jobCategoryService.getAll();
        model.addAttribute("jobCategories", jobCategories);
        model.addAttribute("memberNo", memberNo); // 💡 form hidden input에서 사용됨
        return "company/register";
    }
    @PostMapping("/register")
    public String registerSubmit(@RequestParam("memberNo") Integer memberNo,
                                 CompanyInfoDTO dto,
                                 @RequestParam(value = "jobCategoryIds", required = false) List<Integer> jobCategoryIds) {
        log.info("Company Register Submit: {}", dto);

        // memberNo가 DTO에 없을 경우 set (중요!)
        dto.setMemberNo(memberNo);

        // 회사 정보 등록
        CompanyInfoDTO savedDTO = companyInfoService.register(dto);

        // 직무 카테고리 등록
        if (jobCategoryIds != null && !jobCategoryIds.isEmpty()) {
            companyJobCategoryService.registerCategories(savedDTO.getMemberNo(), jobCategoryIds);
        }

        return "redirect:/member/mypage";
    }


}


