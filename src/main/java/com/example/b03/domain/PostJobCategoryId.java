package com.example.b03.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostJobCategoryId implements java.io.Serializable {
    private Integer postId;
    private Integer jobCategoryId;
}