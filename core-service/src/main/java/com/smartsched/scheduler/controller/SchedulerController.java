package com.smartsched.scheduler.controller;


import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.common.response.ApiResponse;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.scheduler.dto.*;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.scheduler.service.SchedulerService;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;


    @PostMapping("/generate")
    public ApiResponse<TimetableGenerationResponse> generateTimetable(
            @Valid @RequestBody GenerateTimetableRequest request) {

        TimetableGenerationResponse response =
                schedulerService.generateTimetable(request);

        boolean hasFailures =
                response.getFailures() != null
                        && !response.getFailures().isEmpty();

        String message = hasFailures
                ? "Timetable generated, but " + response.getFailures().size()
                + " subject(s) could not be fully scheduled. See failures for details."
                : "Timetable Generated Successfully";

        return new ApiResponse<>(
                true,
                message,
                response,
                LocalDateTime.now()
        );
    }

    @GetMapping
    public ApiResponse<List<TimetableResponse>> getTimetable(
            @RequestParam Long studentClassId,
            @RequestParam Long academicYearId) {

        return new ApiResponse<>(
                true,
                "Timetable Retrieved Successfully",
                schedulerService.getTimetable(
                        studentClassId,
                        academicYearId
                ), LocalDateTime.now()
        );
    }

    @DeleteMapping
    public ApiResponse<String> deleteTimetable(
            @RequestParam Long studentClassId,
            @RequestParam Long academicYearId) {

        schedulerService.deleteTimetable(
                studentClassId,
                academicYearId
        );

        return new ApiResponse<>(
                true,
                "Timetable Deleted Successfully",
                null,LocalDateTime.now()
        );
    }

    @DeleteMapping("/branch/{branchId}/all")
    public ApiResponse<String> deleteAllTimetablesForBranch(
            @PathVariable Long branchId) {

        schedulerService.deleteAllTimetablesForBranch(branchId);

        return new ApiResponse<>(
                true,
                "All previous timetables for this department " +
                        "deleted successfully",
                null,
                LocalDateTime.now()
        );
    }

    /* @PutMapping("/approve")
     public ApiResponse<String> approve(
             @RequestBody ApproveTimetableRequest request){

         schedulerService.approveTimetable(request);

         return new ApiResponse<>(
                 true,
                 "Timetable Updated Successfully",
                 null,
                 LocalDateTime.now()
         );

     }*/
    @GetMapping("/faculty/{facultyId}")
    public ApiResponse<List<FacultyTimetableResponse>>
    getFacultyTimetable(
            @PathVariable Long facultyId){

        return new ApiResponse<>(

                true,

                "Faculty Timetable Retrieved Successfully",

                schedulerService.getFacultyTimetable(facultyId),

                LocalDateTime.now()

        );

    }

    @PutMapping("/approve")
    public ApiResponse<TimetableApprovalResponse> approveTimetable(
            @RequestBody ApproveTimetableRequest request) {

        return new ApiResponse<>(
                true,
                "Timetable approval updated successfully",
                schedulerService.approveTimetable(request),
                LocalDateTime.now()
        );
    }

    @GetMapping("/validate")
    public ApiResponse<TimetableValidationResponse> validateTimetable(
            @RequestParam Long studentClassId,
            @RequestParam Long academicYearId) {

        return new ApiResponse<>(
                true,
                "Timetable validated successfully",
                schedulerService.validateTimetable(
                        studentClassId,
                        academicYearId
                ),
                LocalDateTime.now()
        );
    }

    @PostMapping("/{classId}/regenerate")
    public ApiResponse<String> regenerateTimetable(
            @PathVariable Long classId
    ) {

        schedulerService.regenerateTimetable(classId);

        return new ApiResponse<>(

                true,

                "Timetable Regenerated Successfully",

                "SUCCESS",

                LocalDateTime.now()
        );
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<List<TimetableResponse>> getClassTimetable(
            @PathVariable Long classId) {

        return new ApiResponse<>(
                true,
                "Class Timetable Retrieved Successfully",
                schedulerService.getClassTimetable(classId),
                LocalDateTime.now()
        );
    }
    @PostMapping("/submit")
    public ResponseEntity<String> submitTimetable(
            @Valid @RequestBody SubmitTimetableRequest request) {

        schedulerService.submitTimetable(request);

        return ResponseEntity.ok("Timetable submitted to HOD.");
    }


}