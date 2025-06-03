package com.example.b03.controller;

import com.example.b03.dto.ApplicationDTO;
import com.example.b03.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationApiController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationDTO> create(@RequestBody ApplicationDTO dto) {
        return ResponseEntity.ok(applicationService.create(dto));
    }

    @PutMapping
    public ResponseEntity<ApplicationDTO> update(@RequestBody ApplicationDTO dto) {
        return ResponseEntity.ok(applicationService.update(dto));
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> delete(@PathVariable Integer applicationId) {
        applicationService.delete(applicationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDTO> getById(@PathVariable Integer applicationId) {
        return ResponseEntity.ok(applicationService.getById(applicationId));
    }

    @GetMapping("/member/{memberNo}")
    public ResponseEntity<List<ApplicationDTO>> getByMemberNo(@PathVariable Integer memberNo) {
        return ResponseEntity.ok(applicationService.getByMemberNo(memberNo));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ApplicationDTO>> getByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(applicationService.getByPostId(postId));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyToPost(
            @RequestPart("dto") ApplicationDTO dto,
            @RequestPart("file") MultipartFile file) {
        applicationService.applyToPost(dto, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/company/{companyMemberNo}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByCompany(@PathVariable Integer companyMemberNo) {
        return ResponseEntity.ok(applicationService.getApplicationsByCompany(companyMemberNo));
    }

    @GetMapping("/company/{companyMemberNo}/post/{postId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByPostAndCompany(
            @PathVariable Integer companyMemberNo,
            @PathVariable Integer postId) {
        return ResponseEntity.ok(applicationService.getApplicationsByPostAndCompany(postId, companyMemberNo));
    }

    @GetMapping("/company/{companyMemberNo}/post/{postId}/details")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsWithMemberInfoByPostAndCompany(
            @PathVariable Integer companyMemberNo,
            @PathVariable Integer postId) {
        return ResponseEntity.ok(applicationService.getApplicationsWithMemberInfoByPostAndCompany(postId, companyMemberNo));
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> isDuplicateApplication(
            @RequestParam Integer postId,
            @RequestParam Integer memberNo) {
        return ResponseEntity.ok(applicationService.isDuplicateApplication(postId, memberNo));
    }
}
