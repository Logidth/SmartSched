package com.smartsched.curriculumsubject.service;

import com.smartsched.curriculumsubject.dto.CurriculumSubjectRequest;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;

import java.util.List;

public interface CurriculumSubjectService {

    CurriculumSubjectResponse create(CurriculumSubjectRequest request);

    CurriculumSubjectResponse update(Long id,
                                     CurriculumSubjectRequest request);

    CurriculumSubjectResponse getById(Long id);

    List<CurriculumSubjectResponse> getAll();

    List<CurriculumSubjectResponse> getByCurriculum(Long curriculumId);

    /**
     * Get ONLY the subjects that are actually mapped, via the
     * Curriculum Subject Management screen, to the curriculum
     * identified by (branch, regulation, academic year) - and
     * further filtered down to the given year/semester and to
     * ACTIVE subjects only.
     *
     * This is what a student class's "subjects" should always
     * resolve to; a class must never show every subject that
     * merely shares its Regulation.
     */
    List<CurriculumSubjectResponse> getForStudentClass(
            Long branchId,
            Long regulationId,
            Long academicYearId,
            Integer year,
            Integer semester
    );

    CurriculumSubjectResponse activate(Long id);

    CurriculumSubjectResponse deactivate(Long id);

    void delete(Long id);

}