package com.example.b03.controller;

import com.example.b03.dto.MembershipTypeDTO;
import com.example.b03.service.MembershipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-types")
@RequiredArgsConstructor
public class MembershipTypeController {

    private final MembershipTypeService membershipTypeService;

    @GetMapping
    public ResponseEntity<List<MembershipTypeDTO>> getAll() {
        return ResponseEntity.ok(membershipTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipTypeDTO> get(@PathVariable Byte id) {
        return ResponseEntity.ok(membershipTypeService.getById(id));
    }
}
