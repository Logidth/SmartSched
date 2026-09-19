package com.smartsched.studentclass.service;

import com.smartsched.studentclass.dto.StudentClassRequest;
import com.smartsched.studentclass.dto.StudentClassResponse;

import java.util.List;

public interface StudentClassService {

    StudentClassResponse create(StudentClassRequest request);

    StudentClassResponse update(Long id, StudentClassRequest request);

    StudentClassResponse getById(Long id);

    List<StudentClassResponse> getAll();

    List<StudentClassResponse> getByBranch(Long branchId);

    StudentClassResponse activate(Long id);

    StudentClassResponse deactivate(Long id);

    void delete(Long id);
}