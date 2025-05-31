package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "company_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo extends BaseEntity {
    @Id
    @Column(name = "member_no")
    private Integer memberNo;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_no")
    private Member member;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "founded_date")
    private LocalDate foundedDate;

    @Column(name = "employee_count")
    private Integer employeeCount;

    private Long revenue;

    @Column(name = "tech_stack", length = 255)
    private String techStack;

    @Column(length = 255)
    private String address;

    @Column(name = "homepage_url", length = 255)
    private String homepageUrl;

    @Lob
    private String description;
}
