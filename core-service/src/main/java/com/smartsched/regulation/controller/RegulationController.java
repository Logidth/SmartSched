package com.smartsched.regulation.controller;

import com.smartsched.regulation.dto.RegulationRequest;
import com.smartsched.regulation.dto.RegulationResponse;
import com.smartsched.regulation.service.RegulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regulations")
@RequiredArgsConstructor
public class RegulationController {

    private final RegulationService regulationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegulationResponse create(
            @Valid @RequestBody RegulationRequest request) {

        return regulationService.create(request);
    }

    @PutMapping("/{id}")
    public RegulationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RegulationRequest request) {

        return regulationService.update(id, request);
    }

    @GetMapping("/{id}")
    public RegulationResponse getById(
            @PathVariable Long id) {

        return regulationService.getById(id);
    }

    @GetMapping
    public List<RegulationResponse> getAll() {

        return regulationService.getAll();
    }

    @PatchMapping("/{id}/activate")
    public RegulationResponse activate(
            @PathVariable Long id) {

        return regulationService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    public RegulationResponse deactivate(
            @PathVariable Long id) {

        return regulationService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        regulationService.delete(id);
    }
}