package com.smartsched.report.service;

import com.smartsched.report.dto.BranchReportResponse;
import com.smartsched.report.dto.FacultyWorkloadReportResponse;
import com.smartsched.report.dto.LeaveReportResponse;

import java.util.List;

public interface ReportService {

    List<BranchReportResponse> getBranchReports();

    List<FacultyWorkloadReportResponse> getFacultyWorkloadReport();

    LeaveReportResponse getLeaveReport();
}
