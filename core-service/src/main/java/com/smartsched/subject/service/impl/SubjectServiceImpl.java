package com.smartsched.subject.service.impl;

import com.smartsched.common.enums.Status;
import com.smartsched.common.enums.SubjectType;
import com.smartsched.common.exception.BadRequestException;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.regulation.entity.Regulation;
import com.smartsched.regulation.repository.RegulationRepository;
import com.smartsched.subject.dto.SubjectRequest;
import com.smartsched.subject.dto.SubjectResponse;
import com.smartsched.subject.entity.Subject;
import com.smartsched.subject.mapper.SubjectMapper;
import com.smartsched.subject.repository.SubjectRepository;
import com.smartsched.subject.service.SubjectService;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smartsched.common.enums.SubjectCategory.OPEN_ELECTIVE;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final RegulationRepository regulationRepository;
    private final SubjectMapper mapper;

    @Override
    public SubjectResponse create(SubjectRequest request) {

        validateSubject(request);

        Regulation regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        subjectRepository
                .findBySubjectCodeIgnoreCaseAndRegulationId(
                        request.getSubjectCode(),
                        request.getRegulationId()
                )
                .ifPresent(subject -> {
                    throw new ConflictException("Subject already exists.");
                });

        Subject subject = mapper.toEntity(request);
        subject.setTotalHours(
                request.getTheoryHours() + request.getLabHours()
        );

        subject.setRegulation(regulation);

        Subject saved = subjectRepository.save(subject);

        Subject updated =
                subjectRepository.findByIdWithRegulation(saved.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Subject not found."));

        return mapper.toResponse(updated);
        }

    @Override
    public SubjectResponse update(Long id,
                                  SubjectRequest request) {

        validateSubject(request);

        Subject subject = subjectRepository.findByIdWithRegulation(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));

        Regulation regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        subjectRepository
                .findBySubjectCodeIgnoreCaseAndRegulationId(
                        request.getSubjectCode(),
                        request.getRegulationId()
                )
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new ConflictException("Subject already exists.");

                    }

                });

        mapper.updateEntity(subject, request);
        subject.setTotalHours(
                request.getTheoryHours() + request.getLabHours()
        );

        subject.setRegulation(regulation);

        subjectRepository.save(subject);

        Subject updated = subjectRepository.findByIdWithRegulation(subject.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found."));

        return mapper.toResponse(updated);
    }
    @Transactional(readOnly = true)
    @Override
    public SubjectResponse getById(Long id) {

        Subject subject = subjectRepository.findByIdWithRegulation(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));

        return mapper.toResponse(subject);
    }

    @Override
    public List<SubjectResponse> getAll() {

        return subjectRepository
                .findAllWithRegulation()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<SubjectResponse> getByRegulation(Long regulationId) {

        return subjectRepository
                .findByRegulationIdWithRelations(regulationId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public SubjectResponse activate(Long id) {

        Subject subject = subjectRepository.findByIdWithRegulation(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));

        subject.setStatus(Status.ACTIVE);

        Subject saved = subjectRepository.save(subject);

        return mapper.toResponse(
                subjectRepository.findByIdWithRegulation(saved.getId())
                        .orElseThrow()
        );
    }

    @Override
    public SubjectResponse deactivate(Long id) {

        Subject subject = subjectRepository.findByIdWithRegulation(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));
        subject.setStatus(Status.INACTIVE);

        Subject saved = subjectRepository.save(subject);

        return mapper.toResponse(
                subjectRepository.findByIdWithRegulation(saved.getId())
                        .orElseThrow()
        );
    }

    @Override
    public void delete(Long id) {

        Subject subject = subjectRepository.findByIdWithRegulation(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));

        subject.setStatus(Status.INACTIVE);

        subjectRepository.save(subject);
    }

    private void validateSubject(SubjectRequest request) {

        int calculatedHours = request.getTheoryHours() + request.getLabHours();
        if (request.getSubjectCategory() == null) {
            throw new BadRequestException("Subject category is required.");
        }

        if (calculatedHours <= 0) {
            throw new BadRequestException(
                    "Total subject hours must be greater than zero.");
        }

        if (request.getCredits() <= 0) {
            throw new BadRequestException(
                    "Credits must be greater than zero.");
        }

        if (request.getHoursPerWeek() == null
                || request.getHoursPerWeek() <= 0) {
            throw new BadRequestException(
                    "Hours per week must be greater than zero.");
        }

        if (request.getHoursPerWeek() > calculatedHours) {
            throw new BadRequestException(
                    "Hours per week cannot exceed total hours "
                            + "(theory + lab).");
        }

        switch (request.getSubjectType()) {

            case THEORY -> {

                if (request.getTheoryHours() <= 0) {
                    throw new BadRequestException(
                            "Theory subject must have theory hours.");
                }

                if (request.getLabHours() != 0) {
                    throw new BadRequestException(
                            "Theory subject cannot have lab hours.");
                }

                if (Boolean.TRUE.equals(request.getRequiresLabRoom())) {
                    throw new BadRequestException(
                            "Theory subject cannot require a lab room.");
                }
            }

            case LAB -> {

                if (request.getTheoryHours() != 0) {
                    throw new BadRequestException(
                            "Lab subject cannot have theory hours.");
                }

                if (request.getLabHours() <= 0) {
                    throw new BadRequestException(
                            "Lab subject must have lab hours.");
                }

                if (!Boolean.TRUE.equals(request.getRequiresLabRoom())) {
                    throw new BadRequestException(
                            "Lab subject must require a lab room.");
                }
            }

            case THEORY_LAB -> {

                if (request.getTheoryHours() <= 0) {
                    throw new BadRequestException(
                            "Theory + Lab subject must have theory hours.");
                }

                if (request.getLabHours() <= 0) {
                    throw new BadRequestException(
                            "Theory + Lab subject must have lab hours.");
                }

                if (!Boolean.TRUE.equals(request.getRequiresLabRoom())) {
                    throw new BadRequestException(
                            "Theory + Lab subject must require a lab room.");
                }
            }
        }
    }
}