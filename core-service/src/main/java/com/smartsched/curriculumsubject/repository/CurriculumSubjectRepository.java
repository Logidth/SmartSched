package com.smartsched.curriculumsubject.repository;

import com.smartsched.common.enums.Status;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CurriculumSubjectRepository
        extends JpaRepository<CurriculumSubject, Long> {

    Optional<CurriculumSubject>
    findByCurriculumIdAndSubjectId(
            Long curriculumId,
            Long subjectId
    );

    List<CurriculumSubject> findByCurriculumIdOrderByDisplayOrderAsc(
            Long curriculumId
    );

    List<CurriculumSubject> findByStatus(Status status);

    boolean existsByCurriculumIdAndDisplayOrder(
            Long curriculumId,
            Integer displayOrder
    );

    Optional<CurriculumSubject>
    findByCurriculumIdAndDisplayOrder(
            Long curriculumId,
            Integer displayOrder
    );

    long countByCurriculum_Branch_Id(Long branchId);

    /**
     * Get curriculum subjects filtered by Curriculum + Year + Semester.
     *
     * Used to fetch ONLY the subjects applicable to a specific
     * student class (which has its own Year and Semester).
     *
     * @param curriculum The Curriculum entity (already includes Branch + Regulation + AcademicYear)
     * @param year The academic year (e.g., 1, 2, 3, 4)
     * @param semester The semester (e.g., 1, 2, 3, 4, 5, 6, 7, 8)
     * @return List of matching curriculum subjects, ordered by display order
     */
    List<CurriculumSubject> findByCurriculumAndYearAndSemesterOrderByDisplayOrderAsc(
            Curriculum curriculum,
            Integer year,
            Integer semester
    );

}
