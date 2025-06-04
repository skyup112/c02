package com.example.b03.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsDTO {
    private int totalMembers;
    private int totalPosts;
    private int totalApplications;
}