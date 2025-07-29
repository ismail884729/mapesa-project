package com.example.post.test.DTOs;

import lombok.Data;

@Data
public class DashboardStatsDto {
    private long totalUsers;
    private long activeDrivers;
    private long availableCars;
    private long pendingReports;
}
