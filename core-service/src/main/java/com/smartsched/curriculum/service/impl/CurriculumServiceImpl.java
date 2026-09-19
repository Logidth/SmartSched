package com.smartsched.curriculum.service.impl;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.academicyear.repository.AcademicYearRepository;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.curriculum.dto.CurriculumRequest;
import com.smartsched.curriculum.dto.CurriculumResponse;
import com.smartsched.curriculum.entity.Curriculum;
import com.smartsched.curriculum.mapper.CurriculumMapper;
import com.smartsched.curriculum.repository.CurriculumRepository;
import com.smartsched.curriculum.service.CurriculumService;
import com.smartsched.regulation.entity.Regulation;
import com.smartsched.regulation.repository.RegulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final BranchRepository branchRepository;
    private final RegulationRepository regulationRepository;
    private final AcademicYearRepository academicYearRepository;
    private final CurriculumMapper mapper;
    @Override
    public CurriculumResponse create(CurriculumRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        Regulation regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic year not found."));

        curriculumRepository
                .findByBranchIdAndRegulationIdAndAcademicYearId(
                        request.getBranchId(),
                        request.getRegulationId(),
                        request.getAcademicYearId()
                )
                .ifPresent(c -> {
                    throw new ConflictException("Curriculum already exists.");
                });

        Curriculum curriculum = mapper.toEntity(request);

        curriculum.setBranch(branch);
        curriculum.setRegulation(regulation);
        curriculum.setAcademicYear(academicYear);

        curriculumRepository.save(curriculum);

        Curriculum saved =
                curriculumRepository.findByIdWithRelations(curriculum.getId())
                        .orElseThrow();

        return mapper.toResponse(saved);
    }
    @Override
    public CurriculumResponse update(Long id,
                                     CurriculumRequest request) {

        Curriculum curriculum = curriculumRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        Regulation regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic year not found."));

        curriculumRepository
                .findByBranchIdAndRegulationIdAndAcademicYearId(
                        request.getBranchId(),
                        request.getRegulationId(),
                        request.getAcademicYearId()
                )
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Curriculum already exists.");
                    }

                });

        curriculum.setBranch(branch);
        curriculum.setRegulation(regulation);
        curriculum.setAcademicYear(academicYear);

        curriculumRepository.save(curriculum);

        Curriculum saved =
                curriculumRepository.findByIdWithRelations(curriculum.getId())
                        .orElseThrow();

        return mapper.toResponse(saved);
    }
    @Override
    public CurriculumResponse getById(Long id) {

        Curriculum curriculum = curriculumRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        return mapper.toResponse(curriculum);
    }

    @Override
    public List<CurriculumResponse> getAll() {

        return curriculumRepository
                .findAllWithRelations()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CurriculumResponse> getByBranch(Long branchId) {

        return curriculumRepository.findByBranchIdWithRelations(branchId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CurriculumResponse> getByRegulation(Long regulationId) {

        return curriculumRepository.findByRegulationIdWithRelations(regulationId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CurriculumResponse> getByAcademicYear(Long academicYearId) {

        return curriculumRepository.findByAcademicYearIdWithRelations(academicYearId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CurriculumResponse activate(Long id) {

        Curriculum curriculum = curriculumRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        curriculum.setStatus(Status.ACTIVE);

        curriculumRepository.save(curriculum);

        Curriculum saved =
                curriculumRepository.findByIdWithRelations(curriculum.getId())
                        .orElseThrow();

        return mapper.toResponse(saved);
    }

    @Override
    public CurriculumResponse deactivate(Long id) {

        Curriculum curriculum = curriculumRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        curriculum.setStatus(Status.INACTIVE);

        curriculumRepository.save(curriculum);

        Curriculum saved =
                curriculumRepository.findByIdWithRelations(curriculum.getId())
                        .orElseThrow();

        return mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {

        Curriculum curriculum = curriculumRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curriculum not found."));

        curriculum.setStatus(Status.INACTIVE);

        curriculumRepository.save(curriculum);
    }


}