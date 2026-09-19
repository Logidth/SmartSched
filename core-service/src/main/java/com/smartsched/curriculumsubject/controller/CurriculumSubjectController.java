package com.smartsched.curriculumsubject.controller;

import com.smartsched.curriculumsubject.dto.CurriculumSubjectRequest;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.curriculumsubject.service.CurriculumSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/curriculum-subjects")
@RequiredArgsConstructor
public class CurriculumSubjectController {

    private final CurriculumSubjectService curriculumSubjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CurriculumSubjectResponse create(
            @Valid @RequestBody CurriculumSubjectRequest request) {

        return curriculumSubjectService.create(request);
    }

    @PutMapping("/{id}")
    public CurriculumSubjectResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumSubjectRequest request) {

        return curriculumSubjectService.update(id, request);
    }

    @GetMapping("/{id}")
    public CurriculumSubjectResponse getById(
            @PathVariable Long id) {

        return curriculumSubjectService.getById(id);
    }

    @GetMapping
    public List<CurriculumSubjectResponse> getAll() {

        return curriculumSubjectService.getAll();
    }

    @GetMapping("/curriculum/{curriculumId}")
    public List<CurriculumSubjectResponse> getByCurriculum(
            @PathVariable Long curriculumId) {

        return curriculumSubjectService.getByCurriculum(curriculumId);
    }

    /**
     * Resolve the subjects actually mapped (via Curriculum Subject
     * Management) to a specific student class's branch, regulation,
     * academic year, year and semester - NOT every subject that
     * merely belongs to the same regulation.
     */
    @GetMapping("/for-class")
    public List<CurriculumSubjectResponse> getForStudentClass(
            @RequestParam Long branchId,
            @RequestParam Long regulationId,
            @RequestParam Long academicYearId,
            @RequestParam Integer year,
            @RequestParam Integer semester) {

        return curriculumSubjectService.getForStudentClass(
                branchId, regulationId, academicYearId, year, semester);
    }

    @PatchMapping("/{id}/activate")
    public CurriculumSubjectResponse activate(
            @PathVariable Long id) {

        return curriculumSubjectService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public CurriculumSubjectResponse deactivate(
            @PathVariable Long id) {

        return curriculumSubjectService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        curriculumSubjectService.delete(id);
    }
}