package com.smartsched.report.service.impl;

import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.curriculumsubject.repository.CurriculumSubjectRepository;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.lecture.repository.LectureLogRepository;
import com.smartsched.report.dto.BranchReportResponse;
import com.smartsched.report.dto.FacultyWorkloadReportResponse;
import com.smartsched.report.dto.LeaveReportResponse;
import com.smartsched.report.service.ReportService;
import com.smartsched.room.repository.RoomRepository;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.studentclass.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BranchRepository branchRepository;
    private final FacultyRepository facultyRepository;
    private final StudentClassRepository studentClassRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final RoomRepository roomRepository;
    private final TimetableEntryRepository timetableRepository;
    private final LectureLogRepository lectureRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public List<BranchReportResponse> getBranchReports() {

        return branchRepository.findAll()
                .stream()
                .map(this::buildBranchReport)
                .toList();
    }

    private BranchReportResponse buildBranchReport(Branch branch) {

        long completed =
                lectureRepository.countCompletedByBranch(branch.getId());

        long total =
                lectureRepository.countTotalByBranch(branch.getId());

        double percentage =
                total == 0
                        ? 0
                        : (completed * 100.0) / total;

        return BranchReportResponse.builder()

                .branchId(branch.getId())

                .branchName(branch.getName())

                .branchCode(branch.getCode())

                .facultyCount(
                        facultyRepository.countByBranchId(branch.getId()))

                .classCount(
                        studentClassRepository.countByBranchId(branch.getId()))

                .subjectCount(
                        curriculumSubjectRepository
                                .countByCurriculum_Branch_Id(branch.getId()))

                .roomCount(
                        roomRepository.findByBranchId(branch.getId()).size())

                .approvedTimetables(
                        timetableRepository.countDistinctApprovedTimetablesByBranch(branch.getId()))

                .pendingTimetables(
                        timetableRepository.countDistinctPendingTimetablesByBranch(branch.getId()))
                .completedLectures(completed)

                .totalLectures(total)

                .completionPercentage(
                        Math.round(percentage * 100.0) / 100.0)

                .build();
    }

    @Override
    public List<FacultyWorkloadReportResponse> getFacultyWorkloadReport() {

        return facultyRepository.findAll()
                .stream()
                .map(this::buildFacultyWorkloadReport)
                .toList();
    }

    private FacultyWorkloadReportResponse buildFacultyWorkloadReport(
            Faculty faculty) {

        long weeklyPeriods =
                timetableRepository.countByFacultyId(faculty.getId());

        long completed =
                lectureRepository.countByTimetableEntryFacultyIdAndStatus(
                        faculty.getId(),
                        LectureStatus.COMPLETED);

        long pending =
                lectureRepository.countByTimetableEntryFacultyIdAndStatus(
                        faculty.getId(),
                        LectureStatus.PENDING);

        double percentage =
                weeklyPeriods == 0
                        ? 0
                        : (completed * 100.0) / weeklyPeriods;

        return FacultyWorkloadReportResponse.builder()

                .facultyId(faculty.getId())

                .employeeId(faculty.getEmployeeId())

                .facultyName(faculty.getName())

                .branchName(
                        faculty.getBranch() == null
                                ? null
                                : faculty.getBranch().getName())

                .designation(
                        faculty.getDesignation() == null
                                ? null
                                : faculty.getDesignation().name())

                .weeklyPeriods(weeklyPeriods)

                .completedLectures(completed)

                .pendingLectures(pending)

                .completionPercentage(
                        Math.round(percentage * 100.0) / 100.0)

                .build();
    }

    @Override
    public LeaveReportResponse getLeaveReport() {

        return LeaveReportResponse.builder()

                .totalRequests(leaveRequestRepository.count())

                .pending(
                        leaveRequestRepository.countByStatus(
                                LeaveStatus.PENDING))

                .approved(
                        leaveRequestRepository.countByStatus(
                                LeaveStatus.APPROVED))

                .rejected(
                        leaveRequestRepository.countByStatus(
                                LeaveStatus.REJECTED))

                .build();
    }
}
