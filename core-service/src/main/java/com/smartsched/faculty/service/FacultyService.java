package com.smartsched.faculty.service;

import com.smartsched.faculty.dto.FacultyCredentialsRequest;
import com.smartsched.faculty.dto.FacultyRequest;
import com.smartsched.faculty.dto.FacultyResponse;

import java.util.List;

public interface FacultyService {

    FacultyResponse create(FacultyRequest request);

    /**
     * Creates a login for a faculty member that doesn't have one yet,
     * or resets the password for one that already does.
     */
    FacultyResponse setCredentials(Long id, FacultyCredentialsRequest request);

    FacultyResponse update(Long id, FacultyRequest request);

    FacultyResponse getById(Long id);

    List<FacultyResponse> getAll();

    List<FacultyResponse> getByBranch(Long branchId);

    FacultyResponse activate(Long id);

    FacultyResponse deactivate(Long id);

    void delete(Long id);

}