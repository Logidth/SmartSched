package com.smartsched.facultyavailability.service;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.facultyavailability.dto.FacultyAvailabilityRequest;
import com.smartsched.facultyavailability.entity.FacultyAvailability;
import com.smartsched.facultyavailability.repository.FacultyAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacultyAvailabilityServiceImpl
        implements FacultyAvailabilityService {

    private final FacultyRepository facultyRepository;
    private final FacultyAvailabilityRepository repository;

    @Override
    public void save(FacultyAvailabilityRequest request) {

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow();

        FacultyAvailability availability =
                FacultyAvailability.builder()
                        .faculty(faculty)
                        .day(request.getDay())
                        .period(request.getPeriod())
                        .available(request.getAvailable())
                        .build();

        repository.save(availability);
    }
}