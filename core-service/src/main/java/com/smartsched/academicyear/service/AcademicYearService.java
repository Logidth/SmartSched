package com.smartsched.academicyear.service;

import com.smartsched.academicyear.dto.AcademicYearRequest;
import com.smartsched.academicyear.dto.AcademicYearResponse;

import java.util.List;

public interface AcademicYearService {

    AcademicYearResponse create(AcademicYearRequest request);

    AcademicYearResponse update(Long id,
                                AcademicYearRequest request);

    AcademicYearResponse getById(Long id);

    List<AcademicYearResponse> getAll();

    AcademicYearResponse activate(Long id);

    AcademicYearResponse deactivate(Long id);

    void delete(Long id);
}