package com.smartsched.studentclass.service.impl;

import com.smartsched.academicyear.repository.AcademicYearRepository;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.common.validation.AcademicValidator;
import com.smartsched.regulation.repository.RegulationRepository;
import com.smartsched.studentclass.dto.StudentClassRequest;
import com.smartsched.studentclass.dto.StudentClassResponse;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.studentclass.mapper.StudentClassMapper;
import com.smartsched.studentclass.repository.StudentClassRepository;
import com.smartsched.studentclass.service.StudentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final StudentClassMapper mapper;
    private final AcademicYearRepository academicYearRepository;

    private final RegulationRepository regulationRepository;

    @Override
    public StudentClassResponse create(StudentClassRequest request) {
        AcademicValidator.validateYearSemester(
                request.getYear(),
                request.getSemester()
        );

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        var academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic Year not found."));

        var regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        studentClassRepository
                .findByAcademicYearIdAndRegulationIdAndBranchIdAndYearAndSemesterAndSection(
                        request.getAcademicYearId(),
                        request.getRegulationId(),
                        request.getBranchId(),
                        request.getYear(),
                        request.getSemester(),
                        request.getSection().trim().toUpperCase()
                )
                .ifPresent(sc -> {
                    throw new ConflictException(
                            "Student class already exists for this academic year and regulation.");
                });

        StudentClass studentClass = mapper.toEntity(request);

        studentClass.setBranch(branch);
        studentClass.setAcademicYear(academicYear);
        studentClass.setRegulation(regulation);

        StudentClass saved = studentClassRepository.save(studentClass);

        return mapper.toResponse(saved);
    }
    @Override
    public StudentClassResponse update(Long id, StudentClassRequest request) {
        AcademicValidator.validateYearSemester(
                request.getYear(),
                request.getSemester()
        );
        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student class not found."));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        var academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic Year not found."));

        var regulation = regulationRepository.findById(request.getRegulationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regulation not found."));

        studentClassRepository
                .findByAcademicYearIdAndRegulationIdAndBranchIdAndYearAndSemesterAndSection(
                        request.getAcademicYearId(),
                        request.getRegulationId(),
                        request.getBranchId(),
                        request.getYear(),
                        request.getSemester(),
                        request.getSection().trim().toUpperCase()
                )
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new ConflictException(
                                "Student class already exists for this academic year and regulation.");

                    }

                });

        mapper.updateEntity(studentClass, request);

        studentClass.setBranch(branch);
        studentClass.setAcademicYear(academicYear);
        studentClass.setRegulation(regulation);

        StudentClass updated = studentClassRepository.save(studentClass);

        return mapper.toResponse(updated);
    }
    @Override
    public StudentClassResponse getById(Long id) {

        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student class not found."));

        return mapper.toResponse(studentClass);
    }

    @Override
    public List<StudentClassResponse> getAll() {

        return studentClassRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StudentClassResponse> getByBranch(Long branchId) {

        return studentClassRepository.findByBranchId(branchId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StudentClassResponse activate(Long id) {

        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student class not found."));

        studentClass.setStatus(Status.ACTIVE);

        return mapper.toResponse(studentClassRepository.save(studentClass));
    }

    @Override
    public StudentClassResponse deactivate(Long id) {

        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student class not found."));

        studentClass.setStatus(Status.INACTIVE);

        return mapper.toResponse(studentClassRepository.save(studentClass));
    }

    @Override
    public void delete(Long id) {

        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student class not found."));

        studentClass.setStatus(Status.INACTIVE);

        studentClassRepository.save(studentClass);
    }


}