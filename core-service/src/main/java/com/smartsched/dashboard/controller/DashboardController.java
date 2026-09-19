package com.smartsched.dashboard.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/principal")
    public ApiResponse<?> principalDashboard() {

        return ApiResponse.builder()
                .success(true)
                .message("Principal Dashboard Loaded")
                .data(dashboardService.getPrincipalDashboard())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/faculty/{facultyId}")
    public ApiResponse<?> facultyDashboard(
            @PathVariable Long facultyId) {

        return ApiResponse.builder()
                .success(true)
                .message("Faculty Dashboard")
                .data(dashboardService.facultyDashboard(facultyId))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/hod/{branchId}")
    public ApiResponse<?> hodDashboard(
            @PathVariable Long branchId) {

        return ApiResponse.builder()
                .success(true)
                .message("HOD Dashboard")
                .data(dashboardService.hodDashboard(branchId))
                .timestamp(LocalDateTime.now())
                .build();
    }
}