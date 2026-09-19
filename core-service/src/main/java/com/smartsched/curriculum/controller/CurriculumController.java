package com.smartsched.curriculum.controller;

import com.smartsched.curriculum.dto.CurriculumRequest;
import com.smartsched.curriculum.dto.CurriculumResponse;
import com.smartsched.curriculum.service.CurriculumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CurriculumResponse create(
            @Valid @RequestBody CurriculumRequest request) {

        return curriculumService.create(request);
    }

    @PutMapping("/{id}")
    public CurriculumResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumRequest request) {

        return curriculumService.update(id, request);
    }

    @GetMapping("/{id}")
    public CurriculumResponse getById(
            @PathVariable Long id) {

        return curriculumService.getById(id);
    }

    @GetMapping
    public List<CurriculumResponse> getAll() {

        return curriculumService.getAll();
    }

    @GetMapping("/branch/{branchId}")
    public List<CurriculumResponse> getByBranch(
            @PathVariable Long branchId) {

        return curriculumService.getByBranch(branchId);
    }

    @GetMapping("/regulation/{regulationId}")
    public List<CurriculumResponse> getByRegulation(
            @PathVariable Long regulationId) {

        return curriculumService.getByRegulation(regulationId);
    }

    @GetMapping("/academic-year/{academicYearId}")
    public List<CurriculumResponse> getByAcademicYear(
            @PathVariable Long academicYearId) {

        return curriculumService.getByAcademicYear(academicYearId);
    }

    @PatchMapping("/{id}/activate")
    public CurriculumResponse activate(
            @PathVariable Long id) {

        return curriculumService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public CurriculumResponse deactivate(
            @PathVariable Long id) {

        return curriculumService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        curriculumService.delete(id);
    }
}