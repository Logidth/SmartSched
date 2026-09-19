package com.smartsched.studentclass.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.common.util.ResponseUtil;
import com.smartsched.studentclass.dto.StudentClassRequest;
import com.smartsched.studentclass.dto.StudentClassResponse;
import com.smartsched.studentclass.service.StudentClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentClassResponse>> create(
            @Valid @RequestBody StudentClassRequest request) {

        StudentClassResponse response = studentClassService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success("Student class created successfully.", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentClassResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentClassRequest request) {

        StudentClassResponse response =
                studentClassService.update(id, request);

        return ResponseEntity.ok(
                ResponseUtil.success("Student class updated successfully.", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentClassResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student class retrieved successfully.",
                        studentClassService.getById(id)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentClassResponse>>> getAll() {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student classes retrieved successfully.",
                        studentClassService.getAll()
                ));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<StudentClassResponse>>> getByBranch(
            @PathVariable Long branchId) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student classes retrieved successfully.",
                        studentClassService.getByBranch(branchId)
                ));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<StudentClassResponse>> activate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student class activated successfully.",
                        studentClassService.activate(id)
                ));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<StudentClassResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student class deactivated successfully.",
                        studentClassService.deactivate(id)
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        studentClassService.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        "Student class deleted successfully.",
                        null
                ));
    }
}