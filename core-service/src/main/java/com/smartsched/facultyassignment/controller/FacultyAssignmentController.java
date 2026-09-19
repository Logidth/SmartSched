package com.smartsched.facultyassignment.controller;

import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.facultyassignment.dto.CreateFacultyAssignmentRequest;
import com.smartsched.facultyassignment.dto.FacultyAssignmentResponse;
import com.smartsched.facultyassignment.service.FacultyAssignmentService;
import com.smartsched.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/faculty-assignments")
@RequiredArgsConstructor
public class FacultyAssignmentController {

    private final FacultyAssignmentService service;

    /**
     * Get curriculum subjects filtered by student class.
     *
     * This endpoint returns ONLY the subjects that belong to a
     * specific student class's curriculum (by Branch + Regulation +
     * AcademicYear) AND match the class's year/semester.
     *
     * Used to populate the "Curriculum Subject" dropdown - no more
     * showing unrelated subjects from other curricula.
     */
    @GetMapping("/curriculum-subjects/by-class/{studentClassId}")
    public ApiResponse<List<CurriculumSubjectResponse>> getCurriculumSubjectsForClass(
            @PathVariable Long studentClassId) {

        return new ApiResponse<>(
                true,
                "Curriculum Subjects Retrieved Successfully",
                service.getCurriculumSubjectsForClass(studentClassId),
                LocalDateTime.now()
        );
    }

    @PostMapping
    public ApiResponse<FacultyAssignmentResponse> assignFaculty(
            @Valid @RequestBody CreateFacultyAssignmentRequest request) {

        return new ApiResponse<>(
                true,
                "Faculty Assigned Successfully",
                service.assignFaculty(request), LocalDateTime.now()
        );

    }

    @GetMapping
    public ApiResponse<List<FacultyAssignmentResponse>> getAssignments() {

        return new ApiResponse<>(
                true,
                "Assignments Retrieved Successfully",
                service.getAssignments(),LocalDateTime.now()
        );

    }

    @GetMapping("/{id}")
    public ApiResponse<FacultyAssignmentResponse> getAssignment(
            @PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "Assignment Retrieved Successfully",
                service.getAssignment(id),LocalDateTime.now()
        );

    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAssignment(
            @PathVariable Long id) {

        service.deleteAssignment(id);

        return new ApiResponse<>(
                true,
                "Assignment Deleted Successfully",
                null
                ,LocalDateTime.now());

    }

}
