package com.smartsched.faculty.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import com.smartsched.faculty.dto.FacultyCredentialsRequest;
import com.smartsched.faculty.dto.FacultyRequest;
import com.smartsched.faculty.dto.FacultyResponse;
import com.smartsched.faculty.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponse>> create(
            @Valid @RequestBody FacultyRequest request) {

        FacultyResponse response = facultyService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        "Faculty created successfully.",
                        response
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FacultyRequest request) {

        FacultyResponse response =
                facultyService.update(id, request);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculty updated successfully.",
                        response
                ));
    }

    @PutMapping("/{id}/credentials")
    public ResponseEntity<ApiResponse<FacultyResponse>> setCredentials(
            @PathVariable Long id,
            @Valid @RequestBody FacultyCredentialsRequest request) {

        FacultyResponse response =
                facultyService.setCredentials(id, request);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Login credentials updated successfully.",
                        response
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculty retrieved successfully.",
                        facultyService.getById(id)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getAll() {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculties retrieved successfully.",
                        facultyService.getAll()
                ));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getByBranch(
            @PathVariable Long branchId) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculties retrieved successfully.",
                        facultyService.getByBranch(branchId)
                ));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<FacultyResponse>> activate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculty activated successfully.",
                        facultyService.activate(id)
                ));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<FacultyResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculty deactivated successfully.",
                        facultyService.deactivate(id)
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        facultyService.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Faculty deleted successfully.",
                        null
                ));
    }
}