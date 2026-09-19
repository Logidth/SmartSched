package com.smartsched.curriculum.service;

import com.smartsched.curriculum.dto.CurriculumRequest;
import com.smartsched.curriculum.dto.CurriculumResponse;

import java.util.List;

public interface CurriculumService {

    CurriculumResponse create(CurriculumRequest request);

    CurriculumResponse update(Long id, CurriculumRequest request);

    CurriculumResponse getById(Long id);

    List<CurriculumResponse> getAll();

    List<CurriculumResponse> getByBranch(Long branchId);

    List<CurriculumResponse> getByRegulation(Long regulationId);

    List<CurriculumResponse> getByAcademicYear(Long academicYearId);

    CurriculumResponse activate(Long id);

    CurriculumResponse deactivate(Long id);

    void delete(Long id);

}