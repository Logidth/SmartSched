package com.smartsched.facultyavailability.controller;

import com.smartsched.facultyavailability.dto.FacultyAvailabilityRequest;
import com.smartsched.facultyavailability.service.FacultyAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faculty-availability")
@RequiredArgsConstructor
public class FacultyAvailabilityController {

    private final FacultyAvailabilityService service;

    @PostMapping
    public ResponseEntity<String> save(
            @RequestBody FacultyAvailabilityRequest request
    ) {

        service.save(request);

        return ResponseEntity.ok("Saved");
    }

}