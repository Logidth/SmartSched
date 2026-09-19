package com.smartsched.subject.controller;

import com.smartsched.subject.dto.SubjectRequest;
import com.smartsched.subject.dto.SubjectResponse;
import com.smartsched.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse create(
            @Valid @RequestBody SubjectRequest request) {

        return subjectService.create(request);
    }

    @PutMapping("/{id}")
    public SubjectResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {

        return subjectService.update(id, request);
    }

    @GetMapping("/{id}")
    public SubjectResponse getById(
            @PathVariable Long id) {

        return subjectService.getById(id);
    }

    @GetMapping
    public List<SubjectResponse> getAll() {

        return subjectService.getAll();
    }

    @GetMapping("/regulation/{regulationId}")
    public List<SubjectResponse> getByRegulation(
            @PathVariable Long regulationId) {

        return subjectService.getByRegulation(regulationId);
    }

    @PatchMapping("/{id}/activate")
    public SubjectResponse activate(
            @PathVariable Long id) {

        return subjectService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public SubjectResponse deactivate(
            @PathVariable Long id) {

        return subjectService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        subjectService.delete(id);
    }

}