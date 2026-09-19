package com.smartsched.facultyassignment.service;

import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.facultyassignment.dto.CreateFacultyAssignmentRequest;
import com.smartsched.facultyassignment.dto.FacultyAssignmentResponse;

import java.util.List;

public interface FacultyAssignmentService {

    /**
     * Get curriculum subjects that belong to a specific student class.
     *
     * Filters by:
     * - Student Class's Branch + Regulation + AcademicYear (to get the right Curriculum)
     * - Matching Year and Semester within that Curriculum
     *
     * @param studentClassId ID of the student class
     * @return List of curriculum subjects for that class, mapped to DTO
     */
    List<CurriculumSubjectResponse> getCurriculumSubjectsForClass(
            Long studentClassId
    );

    FacultyAssignmentResponse assignFaculty(
            CreateFacultyAssignmentRequest request
    );

    List<FacultyAssignmentResponse> getAssignments();

    /**
     * All subject assignments allocated to a single faculty member —
     * used by the Faculty self-service "My Subjects" view.
     */
    List<FacultyAssignmentResponse> getAssignmentsByFaculty(Long facultyId);

    FacultyAssignmentResponse getAssignment(Long id);

    void deleteAssignment(Long id);

}