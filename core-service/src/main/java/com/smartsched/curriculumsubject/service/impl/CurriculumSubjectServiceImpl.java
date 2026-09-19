package com.smartsched.curriculumsubject.service.impl;

import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.BadRequestException;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.curriculum.repository.CurriculumRepository;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectRequest;
import com.smartsched.curriculumsubject.dto.CurriculumSubjectResponse;
import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import com.smartsched.curriculumsubject.mapper.CurriculumSubjectMapper;
import com.smartsched.curriculumsubject.repository.CurriculumSubjectRepository;
import com.smartsched.curriculumsubject.service.CurriculumSubjectService;
import com.smartsched.subject.entity.Subject;
import com.smartsched.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumSubjectServiceImpl
        implements CurriculumSubjectService {

    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final CurriculumRepository curriculumRepository;
    private final SubjectRepository subjectRepository;
    private final CurriculumSubjectMapper mapper;

    @Override
    public CurriculumSubjectResponse create(
            CurriculumSubjectRequest request) {

        validate(request);

        Curriculum curriculum = curriculumRepository.findById(
                        request.getCurriculumId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        Subject subject = subjectRepository.findById(
                        request.getSubjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found."));

        curriculumSubjectRepository
                .findByCurriculumIdAndSubjectId(
                        request.getCurriculumId(),
                        request.getSubjectId())
                .ifPresent(cs -> {
                    throw new ConflictException(
                            "Subject already exists in this curriculum.");
                });

        curriculumSubjectRepository
                .findByCurriculumIdAndDisplayOrder(
                        request.getCurriculumId(),
                        request.getDisplayOrder())
                .ifPresent(cs -> {
                    throw new ConflictException(
                            "Display order already exists.");
                });

        validateHoursPerWeek(request, subject);

        CurriculumSubject curriculumSubject =
                mapper.toEntity(request);

        curriculumSubject.setCurriculum(curriculum);
        curriculumSubject.setSubject(subject);

        return mapper.toResponse(
                curriculumSubjectRepository.save(curriculumSubject));
    }

    @Override
    public CurriculumSubjectResponse update(
            Long id,
            CurriculumSubjectRequest request) {

        validate(request);

        CurriculumSubject curriculumSubject =
                curriculumSubjectRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum subject not found."));

        Curriculum curriculum =
                curriculumRepository.findById(request.getCurriculumId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum not found."));

        Subject subject =
                subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subject not found."));

        curriculumSubjectRepository
                .findByCurriculumIdAndSubjectId(
                        request.getCurriculumId(),
                        request.getSubjectId())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new ConflictException(
                                "Subject already exists in curriculum.");

                    }

                });

        curriculumSubjectRepository
                .findByCurriculumIdAndDisplayOrder(
                        request.getCurriculumId(),
                        request.getDisplayOrder())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new ConflictException(
                                "Display order already exists.");

                    }

                });

        validateHoursPerWeek(request, subject);

        mapper.updateEntity(curriculumSubject, request);

        curriculumSubject.setCurriculum(curriculum);
        curriculumSubject.setSubject(subject);

        return mapper.toResponse(
                curriculumSubjectRepository.save(curriculumSubject));
    }

    private void validateHoursPerWeek(
            CurriculumSubjectRequest request,
            Subject subject) {

        if (request.getHoursPerWeek() == null) {
            return;
        }

        if (request.getHoursPerWeek() > subject.getTotalHours()) {
            throw new BadRequestException(
                    "Hours per week override cannot exceed the "
                            + "subject's total hours (theory + lab).");
        }
    }

    private void validate(
            CurriculumSubjectRequest request) {

        if (request.getSemester() !=
                (request.getYear() * 2 - 1)
                &&
                request.getSemester() !=
                        (request.getYear() * 2)) {

            throw new BadRequestException(
                    "Invalid semester for selected year.");
        }

    }

    @Override
    public CurriculumSubjectResponse getById(Long id) {

        CurriculumSubject curriculumSubject =
                curriculumSubjectRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum subject not found."));

        return mapper.toResponse(curriculumSubject);
    }
    @Override
    public List<CurriculumSubjectResponse> getAll() {

        return curriculumSubjectRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CurriculumSubjectResponse> getByCurriculum(Long curriculumId) {

        curriculumRepository.findById(curriculumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curriculum not found."));

        return curriculumSubjectRepository
                .findByCurriculumIdOrderByDisplayOrderAsc(curriculumId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CurriculumSubjectResponse> getForStudentClass(
            Long branchId,
            Long regulationId,
            Long academicYearId,
            Integer year,
            Integer semester) {

        Curriculum curriculum = curriculumRepository
                .findByBranchIdAndRegulationIdAndAcademicYearId(
                        branchId, regulationId, academicYearId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No curriculum has been defined for this " +
                                        "branch, regulation and academic year yet."));

        return curriculumSubjectRepository
                .findByCurriculumAndYearAndSemesterOrderByDisplayOrderAsc(
                        curriculum, year, semester)
                .stream()
                .filter(cs -> cs.getStatus() == Status.ACTIVE)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CurriculumSubjectResponse activate(Long id) {

        CurriculumSubject curriculumSubject =
                curriculumSubjectRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum subject not found."));

        curriculumSubject.setStatus(Status.ACTIVE);

        return mapper.toResponse(
                curriculumSubjectRepository.save(curriculumSubject));
    }

    @Override
    public CurriculumSubjectResponse deactivate(Long id) {

        CurriculumSubject curriculumSubject =
                curriculumSubjectRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum subject not found."));

        curriculumSubject.setStatus(Status.INACTIVE);

        return mapper.toResponse(
                curriculumSubjectRepository.save(curriculumSubject));
    }

    @Override
    public void delete(Long id) {

        CurriculumSubject curriculumSubject =
                curriculumSubjectRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Curriculum subject not found."));

        curriculumSubject.setStatus(Status.INACTIVE);

        curriculumSubjectRepository.save(curriculumSubject);
    }

}