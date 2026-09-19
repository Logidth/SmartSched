package com.smartsched.dashboard.service.impl;

import com.smartsched.attendance.repository.FacultyAttendanceRepository;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.curriculumsubject.repository.CurriculumSubjectRepository;
import com.smartsched.dashboard.dto.DashboardSummaryResponse;
import com.smartsched.dashboard.dto.FacultyDashboardResponse;
import com.smartsched.dashboard.dto.HodDashboardResponse;
import com.smartsched.dashboard.dto.PrincipalDashboardResponse;
import com.smartsched.dashboard.service.DashboardService;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.leave.enums.LeaveStatus;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.lecture.repository.LectureLogRepository;
import com.smartsched.room.repository.RoomRepository;
import com.smartsched.scheduler.enums.TimetableStatus;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.studentclass.repository.StudentClassRepository;
import com.smartsched.subject.repository.SubjectRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BranchRepository branchRepository;
    private final FacultyRepository facultyRepository;
    private final StudentClassRepository studentClassRepository;
    private final SubjectRepository subjectRepository;
    private final RoomRepository roomRepository;
    private final TimetableEntryRepository timetableRepository;
    private final LectureLogRepository lectureRepository;
    private final FacultyAssignmentRepository assignmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final FacultyAttendanceRepository facultyAttendanceRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    @Override
    public PrincipalDashboardResponse principalDashboard() {

        long totalHours =
                curriculumSubjectRepository.findAll()
                        .stream()
                        .mapToLong(cs -> cs.getSubject().getTotalHours())
                        .sum();

        long completedHours =
                lectureRepository.countByStatus(LectureStatus.COMPLETED);

        double progress =
                totalHours == 0
                        ? 0
                        : (completedHours * 100.0) / totalHours;

        return PrincipalDashboardResponse.builder()

                .totalBranches(branchRepository.count())

                .totalFaculties(facultyRepository.count())

                .totalStudentClasses(studentClassRepository.count())

                .totalSubjects(subjectRepository.count())

                .totalRooms(roomRepository.count())

                .totalApprovedTimetables(
                        timetableRepository.countDistinctApprovedTimetables())

                .totalPendingTimetables(
                        timetableRepository.countDistinctPendingTimetables())

                .totalCompletedLectures(
                        lectureRepository.countByStatus(
                                LectureStatus.COMPLETED))

                .overallProgress(progress)

                .build();
    }

    @Override
    public FacultyDashboardResponse facultyDashboard(Long facultyId) {

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty Not Found"));

        long completedHours =
                lectureRepository.countByTimetableEntryFacultyIdAndStatus(
                        facultyId,
                        LectureStatus.COMPLETED);

        long totalHours =
                timetableRepository.countByFacultyId(facultyId);

        double completion =
                totalHours == 0
                        ? 0
                        : (completedHours * 100.0) / totalHours;

        return FacultyDashboardResponse.builder()

                .facultyId(faculty.getId())

                .facultyName(faculty.getName())

                .assignedSubjects(
                        assignmentRepository.countByFacultyId(facultyId))

                .weeklyPeriods(
                        timetableRepository.countByFacultyId(facultyId))

                .completedLectures(
                        lectureRepository.countByTimetableEntryFacultyIdAndStatus(
                                facultyId,
                                LectureStatus.COMPLETED))

                .pendingLectures(
                        lectureRepository.countByTimetableEntryFacultyIdAndStatus(
                                facultyId,
                                LectureStatus.PENDING))

                .completionPercentage(completion)

                .build();
    }
    @Override
    public HodDashboardResponse hodDashboard(Long branchId) {

        long approved =
                timetableRepository.countDistinctApprovedTimetablesByBranch(branchId);

        long pending =
                timetableRepository.countDistinctPendingTimetablesByBranch(branchId);

        long completed =
                lectureRepository.countCompletedByBranch(branchId);

        long total =
                lectureRepository.countTotalByBranch(branchId);

        double percentage =
                total == 0
                        ? 0
                        : (completed * 100.0) / total;

        return HodDashboardResponse.builder()

                .branch(
                        branchRepository.findById(branchId)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Branch Not Found"))
                                .getName())

                .facultyCount(
                        facultyRepository.countByBranchId(branchId))

                .classCount(
                        studentClassRepository.countByBranchId(branchId))

                .subjectCount(
                        curriculumSubjectRepository.countByCurriculum_Branch_Id(branchId))

                .approvedTimetables(approved)

                .pendingTimetables(pending)

                .completionPercentage(percentage)

                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getPrincipalDashboard(){

        return DashboardSummaryResponse.builder()

                .facultyCount(facultyRepository.count())

                .branchCount(branchRepository.count())

                .subjectCount(subjectRepository.count())

                .classCount(studentClassRepository.count())

                .pendingLeaves(
                        leaveRequestRepository.countByStatus(LeaveStatus.PENDING))

                .pendingTimetables(
                        timetableRepository.countDistinctByStatus(
                                TimetableStatus.SUBMITTED))

                .todayAttendance(
                        facultyAttendanceRepository.countPresentToday())

                .build();
    }
}