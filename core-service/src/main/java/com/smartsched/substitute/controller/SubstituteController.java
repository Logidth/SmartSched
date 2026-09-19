package com.smartsched.substitute.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.substitute.dto.AssignSubstituteRequest;
import com.smartsched.substitute.dto.SubstituteResponse;
import com.smartsched.substitute.service.SubstituteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/substitute")
@RequiredArgsConstructor
public class SubstituteController {

    private final SubstituteService substituteService;

    @PostMapping("/assign")
    public ApiResponse<SubstituteResponse> assignSubstitute(
            @RequestBody AssignSubstituteRequest request) {

        return new ApiResponse<>(
                true,
                "Substitute Assigned Successfully",
                substituteService.assignSubstitute(request),
                LocalDateTime.now()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<SubstituteResponse> getAssignment(
            @PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "Assignment Retrieved Successfully",
                substituteService.getAssignment(id),
                LocalDateTime.now()
        );
    }

    @GetMapping("/faculty/{facultyId}")
    public ApiResponse<List<SubstituteResponse>> getFacultyAssignments(
            @PathVariable Long facultyId) {

        return new ApiResponse<>(
                true,
                "Faculty Assignments Retrieved Successfully",
                substituteService.getFacultyAssignments(facultyId),
                LocalDateTime.now()
        );
    }

    @GetMapping
    public ApiResponse<List<SubstituteResponse>> getAllAssignments() {

        return new ApiResponse<>(
                true,
                "Substitute Assignments Retrieved Successfully",
                substituteService.getAllAssignments(),
                LocalDateTime.now()
        );
    }
}