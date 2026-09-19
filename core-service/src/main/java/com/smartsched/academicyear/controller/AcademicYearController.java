package com.smartsched.academicyear.controller;

import com.smartsched.academicyear.dto.AcademicYearRequest;
import com.smartsched.academicyear.dto.AcademicYearResponse;
import com.smartsched.academicyear.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicYearResponse create(
            @Valid @RequestBody AcademicYearRequest request) {

        return academicYearService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicYearResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicYearRequest request) {

        return academicYearService.update(id, request);
    }

    @GetMapping("/{id}")
    public AcademicYearResponse getById(
            @PathVariable Long id) {

        return academicYearService.getById(id);
    }

    @GetMapping
    public List<AcademicYearResponse> getAll() {

        return academicYearService.getAll();
    }

    @PatchMapping("/{id}/activate")
    public AcademicYearResponse activate(
            @PathVariable Long id) {

        return academicYearService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public AcademicYearResponse deactivate(
            @PathVariable Long id) {

        return academicYearService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        academicYearService.delete(id);
    }
}