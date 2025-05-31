package com.example.b03.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// 복합키 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJobCategoryId implements java.io.Serializable {
    private Integer memberNo;
    private Integer jobCategoryId;
}
