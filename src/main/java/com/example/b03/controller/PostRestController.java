package com.example.b03.controller;

import com.example.b03.dto.PostDTO;
import com.example.b03.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable Integer postId) {
        PostDTO post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("postId", post.getPostId());
        map.put("title", post.getTitle());
        map.put("description", post.getDescription());
        map.put("address", post.getAddress());
        map.put("salary", post.getSalary());
        map.put("postedDate", post.getPostedDate());
        map.put("deadline", post.getDeadline());

        return ResponseEntity.ok(map);
    }
}
