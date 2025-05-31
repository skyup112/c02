package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_job_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CompanyJobCategoryId.class)
public class CompanyJobCategory {

    @Id
    @Column(name = "member_no")
    private Integer memberNo;

    @Id
    @Column(name = "job_category_id")
    private Integer jobCategoryId;

    @ManyToOne
    @MapsId("memberNo")
    @JoinColumn(name = "member_no")
    private CompanyInfo company;

    @ManyToOne
    @MapsId("jobCategoryId")
    @JoinColumn(name = "job_category_id")
    private JobCategory jobCategory;
}
