package com.smartsched.academicyear.service.impl;

import com.smartsched.academicyear.dto.AcademicYearRequest;
import com.smartsched.academicyear.dto.AcademicYearResponse;
import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.academicyear.mapper.AcademicYearMapper;
import com.smartsched.academicyear.repository.AcademicYearRepository;
import com.smartsched.academicyear.service.AcademicYearService;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper mapper;

    @Override
    public AcademicYearResponse create(AcademicYearRequest request) {

        academicYearRepository
                .findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Academic year already exists.");
                });

        AcademicYear academicYear = mapper.toEntity(request);

        return mapper.toResponse(
                academicYearRepository.save(academicYear));
    }


    @Override
    public AcademicYearResponse update(
            Long id,
            AcademicYearRequest request) {

        AcademicYear academicYear =
                academicYearRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found."));

        academicYearRepository
                .findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new ConflictException(
                                "Academic year already exists.");

                    }

                });

        mapper.updateEntity(academicYear, request);

        return mapper.toResponse(
                academicYearRepository.save(academicYear));
    }

    @Override
    public AcademicYearResponse getById(Long id) {

        AcademicYear academicYear =
                academicYearRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found."));

        return mapper.toResponse(academicYear);
    }

    @Override
    public List<AcademicYearResponse> getAll() {

        return academicYearRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AcademicYearResponse activate(Long id) {

        AcademicYear academicYear =
                academicYearRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found."));

        academicYear.setStatus(Status.ACTIVE);

        return mapper.toResponse(
                academicYearRepository.save(academicYear));
    }

    @Override
    public AcademicYearResponse deactivate(Long id) {

        AcademicYear academicYear =
                academicYearRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found."));

        academicYear.setStatus(Status.INACTIVE);

        return mapper.toResponse(
                academicYearRepository.save(academicYear));
    }

    @Override
    public void delete(Long id) {

        AcademicYear academicYear =
                academicYearRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found."));

        academicYear.setStatus(Status.INACTIVE);

        academicYearRepository.save(academicYear);
    }
}