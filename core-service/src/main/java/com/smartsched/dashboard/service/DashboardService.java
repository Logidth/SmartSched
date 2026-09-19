package com.smartsched.dashboard.service;

import com.smartsched.dashboard.dto.DashboardSummaryResponse;
import com.smartsched.dashboard.dto.FacultyDashboardResponse;
import com.smartsched.dashboard.dto.HodDashboardResponse;
import com.smartsched.dashboard.dto.PrincipalDashboardResponse;

public interface DashboardService {



    FacultyDashboardResponse facultyDashboard(Long facultyId);

    HodDashboardResponse hodDashboard(Long branchId);

    PrincipalDashboardResponse principalDashboard();

    DashboardSummaryResponse getPrincipalDashboard();



}