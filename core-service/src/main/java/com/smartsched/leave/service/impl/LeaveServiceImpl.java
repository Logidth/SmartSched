package com.smartsched.leave.service.impl;

import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.leave.dto.CreateLeaveRequest;
import com.smartsched.leave.dto.LeaveApprovalRequest;
import com.smartsched.leave.dto.LeaveResponse;
import com.smartsched.leave.entity.LeaveRequest;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.mapper.LeaveMapper;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    private final FacultyRepository facultyRepository;

    private final LeaveMapper leaveMapper;

    private Faculty getFaculty(Long id) {

        return facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty Not Found"));

    }

    private LeaveRequest getLeaveRequest(Long id) {

        return leaveRequestRepository.findByIdWithFacultyAndBranch(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave Request Not Found"));

    }

    @Override
    public LeaveResponse applyLeave(
            CreateLeaveRequest request) {

        Faculty faculty = getFaculty(request.getFacultyId());

        boolean exists =
                leaveRequestRepository
                        .existsByFacultyAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                faculty,
                                LeaveStatus.APPROVED,
                                request.getToDate(),
                                request.getFromDate()
                        );

        if (exists) {

            throw new ResourceNotFoundException(
                    "Approved leave already exists for these dates.");

        }
        boolean overlap =
                leaveRequestRepository
                        .existsByFacultyAndStatusInAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                faculty,
                                List.of(
                                        LeaveStatus.PENDING,
                                        LeaveStatus.APPROVED
                                ),
                                request.getToDate(),
                                request.getFromDate()
                        );

        if (overlap) {
            throw new ResourceNotFoundException(
                    "An overlapping leave request already exists."
            );
        }

        LeaveRequest leave = new LeaveRequest();

        leave.setFaculty(faculty);

        leave.setLeaveType(request.getLeaveType());

        leave.setFromDate(request.getFromDate());

        leave.setToDate(request.getToDate());

        leave.setReason(request.getReason());

        leave.setStatus(LeaveStatus.PENDING);

        leave = leaveRequestRepository.save(leave);

        return leaveMapper.map(leave);

    }

    @Override
    public LeaveResponse approveLeave(
            LeaveApprovalRequest request) {

        LeaveRequest leave =
                getLeaveRequest(request.getLeaveRequestId());

        leave.setStatus(LeaveStatus.APPROVED);

        leave.setApprovalRemarks(request.getRemarks());

        leave = leaveRequestRepository.save(leave);

        return leaveMapper.map(leave);

    }

    @Override
    public LeaveResponse rejectLeave(
            LeaveApprovalRequest request) {

        LeaveRequest leave =
                getLeaveRequest(request.getLeaveRequestId());

        leave.setStatus(LeaveStatus.REJECTED);

        leave.setApprovalRemarks(request.getRemarks());

        leave = leaveRequestRepository.save(leave);

        return leaveMapper.map(leave);

    }

    @Override
    public LeaveResponse getLeave(Long id) {

        return leaveMapper.map(getLeaveRequest(id));

    }

    @Override
    public List<LeaveResponse> getFacultyLeaves(
            Long facultyId) {

        Faculty faculty = getFaculty(facultyId);

        return leaveRequestRepository.findByFaculty(faculty)
                .stream()
                .map(leaveMapper::map)
                .toList();

    }

    @Override
    public List<LeaveResponse> getPendingLeaves() {

        return leaveRequestRepository.findByStatus(
                        LeaveStatus.PENDING)
                .stream()
                .map(leaveMapper::map)
                .toList();

    }

    @Override
    public List<LeaveResponse> getAllLeaves() {

        return leaveRequestRepository.findAllWithFacultyAndBranch()
                .stream()
                .map(leaveMapper::map)
                .toList();

    }

    // ============================================================
    // HOD (department-scoped) variants
    // ============================================================

    /**
     * Ensures the given leave request belongs to a faculty member of
     * the specified branch, so an HOD cannot approve/reject leave
     * requests raised outside their own department.
     */
    private LeaveRequest getLeaveRequestForBranch(
            Long leaveRequestId,
            Long branchId
    ) {

        LeaveRequest leave = getLeaveRequest(leaveRequestId);

        Long leaveBranchId =
                leave.getFaculty().getBranch() == null
                        ? null
                        : leave.getFaculty().getBranch().getId();

        if (leaveBranchId == null || !leaveBranchId.equals(branchId)) {

            throw new ResourceNotFoundException(
                    "You are not allowed to act on leave requests outside your department."
            );
        }

        return leave;
    }

    @Override
    public List<LeaveResponse> getPendingLeavesForBranch(
            Long branchId) {

        return leaveRequestRepository
                .findByStatusAndFacultyBranchId(
                        LeaveStatus.PENDING,
                        branchId
                )
                .stream()
                .map(leaveMapper::map)
                .toList();

    }

    @Override
    public LeaveResponse approveLeaveForBranch(
            LeaveApprovalRequest request,
            Long branchId) {

        LeaveRequest leave =
                getLeaveRequestForBranch(
                        request.getLeaveRequestId(),
                        branchId
                );

        leave.setStatus(LeaveStatus.APPROVED);

        leave.setApprovalRemarks(request.getRemarks());

        leave = leaveRequestRepository.save(leave);

        return leaveMapper.map(leave);

    }

    @Override
    public LeaveResponse rejectLeaveForBranch(
            LeaveApprovalRequest request,
            Long branchId) {

        LeaveRequest leave =
                getLeaveRequestForBranch(
                        request.getLeaveRequestId(),
                        branchId
                );

        leave.setStatus(LeaveStatus.REJECTED);

        leave.setApprovalRemarks(request.getRemarks());

        leave = leaveRequestRepository.save(leave);

        return leaveMapper.map(leave);

    }

}