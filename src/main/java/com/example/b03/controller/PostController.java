package com.example.b03.controller;

import com.example.b03.dto.PostDTO;
import com.example.b03.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> create(@RequestBody PostDTO dto) {
        return ResponseEntity.ok(postService.createPost(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Integer id, @RequestBody PostDTO dto) {
        dto.setPostId(id);
        return ResponseEntity.ok(postService.updatePost(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> get(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping("/company/{memberNo}")
    public ResponseEntity<List<PostDTO>> getByCompany(@PathVariable Integer memberNo) {
        return ResponseEntity.ok(postService.getPostsByCompany(memberNo));
    }
}
