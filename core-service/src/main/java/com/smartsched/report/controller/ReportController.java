package com.smartsched.report.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.report.dto.BranchReportResponse;
import com.smartsched.report.dto.FacultyWorkloadReportResponse;
import com.smartsched.report.dto.LeaveReportResponse;
import com.smartsched.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/principal/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/branches")
    public ApiResponse<List<BranchReportResponse>> getBranchReports() {

        return ApiResponse.<List<BranchReportResponse>>builder()
                .success(true)
                .message("Branch Report Retrieved Successfully")
                .data(reportService.getBranchReports())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/faculty-workload")
    public ApiResponse<List<FacultyWorkloadReportResponse>> getFacultyWorkloadReport() {

        return ApiResponse.<List<FacultyWorkloadReportResponse>>builder()
                .success(true)
                .message("Faculty Workload Report Retrieved Successfully")
                .data(reportService.getFacultyWorkloadReport())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/leaves")
    public ApiResponse<LeaveReportResponse> getLeaveReport() {

        return ApiResponse.<LeaveReportResponse>builder()
                .success(true)
                .message("Leave Report Retrieved Successfully")
                .data(reportService.getLeaveReport())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
