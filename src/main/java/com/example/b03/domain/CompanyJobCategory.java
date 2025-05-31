package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_job_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CompanyJobCategoryId.class)
public class CompanyJobCategory {
    @Id
    private Integer memberNo;

    @Id
    private Integer jobCategoryId;

    @ManyToOne
    @JoinColumn(name = "member_no", insertable = false, updatable = false)
    private CompanyInfo company;

    @ManyToOne
    @JoinColumn(name = "job_category_id", insertable = false, updatable = false)
    private JobCategory jobCategory;
}

