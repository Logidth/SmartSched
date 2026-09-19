package com.smartsched.workload.service;

import com.smartsched.workload.dto.FacultyWorkloadResponse;

public interface FacultyWorkloadService {

    FacultyWorkloadResponse getWorkload(Long facultyId);

    void calculateWorkload();

}