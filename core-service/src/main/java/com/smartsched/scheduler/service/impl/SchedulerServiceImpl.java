package com.smartsched.scheduler.service.impl;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.academicyear.repository.AcademicYearRepository;
import com.smartsched.auth.entity.User;
import com.smartsched.auth.repository.UserRepository;
import com.smartsched.common.enums.Role;
import com.smartsched.common.exception.BadRequestException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.curriculumsubject.repository.CurriculumSubjectRepository;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.leave.repository.LeaveRequestRepository;
import com.smartsched.notification.enums.NotificationType;
import com.smartsched.notification.service.NotificationService;
import com.smartsched.room.repository.RoomRepository;
import com.smartsched.scheduler.algorithm.CandidateGenerator;
import com.smartsched.scheduler.algorithm.ConstraintChecker;
import com.smartsched.scheduler.dto.*;
import com.smartsched.scheduler.engine.SchedulerContext;
import com.smartsched.scheduler.engine.SchedulerEngine;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.engine.TimetableGenerationResult;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.TimetableStatus;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.scheduler.exception.TimetableGenerationException;
import com.smartsched.scheduler.repository.TimetableEntryRepository;
import com.smartsched.scheduler.service.SchedulerService;
import com.smartsched.security.service.CurrentUserService;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.studentclass.repository.StudentClassRepository;
import com.smartsched.subject.entity.Subject;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final SchedulerEngine schedulerEngine;

    private final FacultyAssignmentRepository facultyAssignmentRepository;

    private final RoomRepository roomRepository;

    private final TimetableEntryRepository timetableRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    private final StudentClassRepository studentClassRepository;

    private final AcademicYearRepository academicYearRepository;

    private final CurrentUserService currentUserService;

    private final ConstraintChecker constraintChecker;

    private final FacultyRepository facultyRepository;

    private final CandidateGenerator candidateGenerator;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;


    // ============================================================
    // HELPER METHODS
    // ============================================================

    private StudentClass getStudentClass(Long id) {

        return studentClassRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Class Not Found"
                        )
                );
    }


    private AcademicYear getAcademicYear(Long id) {

        return academicYearRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Academic Year Not Found"
                        )
                );
    }


    /**
     * Every StudentClass already belongs to exactly one AcademicYear
     * (student_classes.academic_year_id). The timetable endpoints also
     * accept an academicYearId in the request/query string, which is
     * meant to just be that same academic year.
     *
     * If the two ever disagree (e.g. the caller picked a different
     * academic year than the one the class actually belongs to), the
     * generated timetable entries end up saved under an academic year
     * that has no matching FacultyAssignment/Curriculum data. Loading
     * the timetable back with the "correct" academic year then shows
     * it as missing entries - even though the faculty assignment
     * (e.g. "Matrices" for a class) exists and was used correctly.
     *
     * Fail fast here instead of silently generating/saving a timetable
     * under a mismatched academic year.
     */
    private void validateAcademicYearMatchesClass(
            StudentClass studentClass,
            AcademicYear academicYear
    ) {

        if (studentClass.getAcademicYear() == null) {

            throw new BadRequestException(
                    "Student class '"
                            + studentClass.getBranch().getName()
                            + " Year " + studentClass.getYear()
                            + " Sem " + studentClass.getSemester()
                            + "' has no academic year assigned."
            );
        }

        if (!studentClass.getAcademicYear()
                .getId()
                .equals(academicYear.getId())) {

            throw new BadRequestException(
                    "Selected academic year '"
                            + academicYear.getName()
                            + "' does not match the academic year '"
                            + studentClass.getAcademicYear().getName()
                            + "' that this class belongs to. "
                            + "Select the matching academic year for this class."
            );
        }
    }


    /**
     * Checks whether the currently logged-in admin is allowed
     * to access this department's timetable.
     *
     * ADMIN users are restricted to their own branch.
     *
     * Other roles are currently allowed through.
     */
    private void validateDepartmentAccess(
            StudentClass studentClass
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            if (currentUser.getBranch() == null) {

                throw new ResourceNotFoundException(
                        "Admin has no branch assigned."
                );
            }

            if (!currentUser.getBranch()
                    .getId()
                    .equals(studentClass.getBranch().getId())) {

                throw new ResourceNotFoundException(
                        "You are not allowed to access another department timetable."
                );
            }
        }
    }


    // ============================================================
    // GENERATE TIMETABLE
    // ============================================================

    @Override
    @Transactional
    public TimetableGenerationResponse generateTimetable(
            GenerateTimetableRequest request
    ) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(
                        request.getAcademicYearId()
                );

        validateAcademicYearMatchesClass(
                studentClass,
                academicYear
        );


        /*
         * Serialize concurrent "generate timetable" requests for this
         * class.
         *
         * The flush() below already fixes the SEQUENTIAL regenerate
         * case (old rows physically gone before we insert new ones
         * in the same request). It does nothing for two OVERLAPPING
         * requests for the same class - e.g. a retried network call,
         * or two admins hitting "Generate" for the same class at
         * the same time. Both transactions would see zero old
         * entries, independently compute their own (each internally
         * conflict-free) timetable, and then both try to INSERT into
         * the same (student_class_id, academic_year_id, day, period)
         * slot - exactly the DataIntegrityViolationException seen in
         * production logs.
         *
         * Taking a pessimistic write lock on the StudentClass row
         * here makes the second transaction block until the first
         * COMMITS. It then re-reads the now-committed data as its
         * own "old entries" below and regenerates cleanly instead of
         * racing.
         */
        try {

            studentClassRepository
                    .findByIdForUpdate(studentClass.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Class Not Found"
                            )
                    );

        } catch (PessimisticLockingFailureException lockTimeout) {

            /*
             * Someone else's generateTimetable call for this exact
             * class is still holding the row lock (see the comment
             * above). Rather than letting the raw
             * PessimisticLockingFailureException / "Lock wait
             * timeout exceeded" bubble up as a 500, surface it as
             * the same kind of clean, retryable message used for
             * the cross-class conflict case below.
             */
            throw new TimetableGenerationException(
                    "Timetable generation for class "
                            + studentClass.getId()
                            + " is already in progress in another "
                            + "request. Please wait a moment and "
                            + "try again.",
                    List.of()
            );
        }


        /*
         * Delete previous timetable for this class
         * and academic year.
         */
        List<TimetableEntry> oldEntries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );

        if (!oldEntries.isEmpty()) {

            timetableRepository.deleteAll(oldEntries);

            /*
             * TimetableEntry uses GenerationType.IDENTITY, so the
             * INSERTs below execute immediately (Hibernate has no
             * choice - it needs the DB-generated id right away).
             * The DELETEs above, however, are queued and would
             * normally only be flushed at the end of the
             * transaction (Hibernate flushes deletes last).
             *
             * Without this explicit flush, saveAll() below can
             * try to insert a new row for e.g. (studentClass=9,
             * academicYear=1, MONDAY, period=1) while the OLD row
             * at that exact same unique key is still physically
             * present in the table, causing a
             * DataIntegrityViolationException on regenerate.
             */
            timetableRepository.flush();
        }


        /*
         * Get faculty assignments for this class.
         */
        List<FacultyAssignment> assignments =
                facultyAssignmentRepository
                        .findByStudentClass(studentClass);


        if (assignments.isEmpty()) {

            throw new IllegalStateException(
                    "No faculty assignments found for class "
                            + studentClass.getId()
            );
        }


        /*
         * Build scheduler context.
         */
        SchedulerContext context =
                SchedulerContext.builder()
                        .studentClass(studentClass)
                        .academicYear(academicYear)
                        .assignments(assignments)
                        .rooms(roomRepository.findAll())
                        .workingDays(
                                WorkingDay.values().length
                        )
                        .periodsPerDay(7)
                        .build();


        /*
         * Scheduler state.
         */
        SchedulerState state =
                new SchedulerState();


        /*
         * Load existing timetable entries
         * belonging to other classes.
         *
         * This is important because faculty and room
         * conflicts can exist across departments.
         */
        state.loadExisting(
                timetableRepository.findByAcademicYear(
                        academicYear
                )
        );


        /*
         * Generate timetable.
         *
         * The engine no longer aborts the whole class just because one
         * subject couldn't be placed (e.g. every eligible faculty is
         * already at their weekly cap). It keeps going and reports any
         * such gaps in result.getFailures().
         */
        TimetableGenerationResult result =
                schedulerEngine.generate(
                        context,
                        state
                );

        List<TimetableEntry> generatedEntries =
                result.getEntries();


        /*
         * If NOTHING could be generated at all, there is nothing
         * meaningful to save - surface this as a hard failure like
         * before.
         */
        if (generatedEntries.isEmpty()) {

            throw new TimetableGenerationException(
                    "Unable to generate any part of the timetable for class "
                            + studentClass.getId()
                            + ". "
                            + String.join(" | ", result.getFailures()),
                    result.getFailures()
            );
        }


        /*
         * Save whatever WAS successfully generated, even if some
         * subjects couldn't be placed.
         *
         * The pessimistic lock above already rules out the
         * same-class race that used to cause this. This catch is a
         * defense-in-depth safety net for the narrower remaining
         * case - two DIFFERENT classes being regenerated at the same
         * instant and happening to collide on a shared faculty/room
         * slot - so the caller gets a clear, retryable message
         * instead of a raw 500 with a Hibernate stack trace.
         */
        try {

            timetableRepository.saveAll(
                    generatedEntries
            );

        } catch (DataIntegrityViolationException conflict) {

            throw new TimetableGenerationException(
                    "Timetable generation for class "
                            + studentClass.getId()
                            + " conflicted with another timetable "
                            + "being generated at the same time "
                            + "(shared faculty or room slot). "
                            + "Please try generating again.",
                    result.getFailures()
            );
        }


        /*
         * Surface partial failures without discarding the entries we
         * just saved, e.g. "Matrices" (MX121) getting 0 of its
         * required hours because every eligible faculty was already
         * at their weekly cap, or there were no free periods left in
         * the week once other subjects were placed. Previously this
         * only went to the server log - the API always said
         * "Timetable Generated Successfully" even when a whole
         * subject was silently dropped. The client can also call
         * validateTimetable(...) afterwards, which already checks
         * allocated vs required hours per subject and will show the
         * same gap.
         */
        if (result.hasFailures()) {

            System.err.println(
                    "[SCHEDULER] Timetable for class "
                            + studentClass.getId()
                            + " generated with "
                            + result.getFailures().size()
                            + " unallocated subject(s):"
            );

            result.getFailures().forEach(
                    failure -> System.err.println("  - " + failure)
            );
        }


        /*
         * Return response.
         */
        return TimetableGenerationResponse.builder()

                .entries(
                        generatedEntries
                                .stream()
                                .map(this::map)
                                .toList()
                )

                .failures(
                        result.getFailures()
                )

                .build();
    }


    // ============================================================
    // GET TIMETABLE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    // Without this, the Hibernate session closes as soon as the
    // repository call returns, and the later .map(this::map) call
    // (which lazily touches studentClass.getBranch() etc. on each
    // entry) throws LazyInitializationException: no session.
    public List<TimetableResponse> getTimetable(
            Long studentClassId,
            Long academicYearId
    ) {

        StudentClass studentClass =
                getStudentClass(studentClassId);

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(academicYearId);

        validateAcademicYearMatchesClass(
                studentClass,
                academicYear
        );


        return timetableRepository
                .findByStudentClass(studentClass)
                .stream()
                .filter(entry ->
                        entry.getAcademicYear()
                                .getId()
                                .equals(
                                        academicYear.getId()
                                )
                )
                .map(this::map)
                .toList();
    }


    // ============================================================
    // DELETE TIMETABLE
    // ============================================================

    @Override
    @Transactional
    public void deleteTimetable(
            Long studentClassId,
            Long academicYearId
    ) {

        StudentClass studentClass =
                getStudentClass(studentClassId);

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(academicYearId);


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (!entries.isEmpty()) {
            timetableRepository.deleteAll(entries);
        }
    }


    // ============================================================
    // DELETE ALL TIMETABLES FOR A BRANCH (DEPARTMENT)
    // ============================================================

    @Override
    @Transactional
    public void deleteAllTimetablesForBranch(Long branchId) {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            if (currentUser.getBranch() == null) {

                throw new ResourceNotFoundException(
                        "Admin has no branch assigned."
                );
            }

            if (!currentUser.getBranch()
                    .getId()
                    .equals(branchId)) {

                throw new ResourceNotFoundException(
                        "You are not allowed to delete another " +
                                "department's timetables."
                );
            }
        }

        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassBranchId(branchId);

        if (!entries.isEmpty()) {
            timetableRepository.deleteAll(entries);
        }
    }
    // ============================================================
    // SUBMIT TIMETABLE
    // ============================================================

    @Override
    @Transactional
    public void submitTimetable(
            SubmitTimetableRequest request
    ) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(
                        request.getAcademicYearId()
                );


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (entries.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No timetable found."
            );
        }


        for (TimetableEntry entry : entries) {

            if (entry.getStatus()
                    == TimetableStatus.APPROVED) {

                throw new IllegalStateException(
                        "Approved timetable cannot be submitted again."
                );
            }


            entry.setStatus(
                    TimetableStatus.SUBMITTED
            );

            entry.setRemarks(
                    request.getRemarks()
            );

            entry.setApproved(false);

            entry.setApprovedAt(null);

            entry.setHodApprovedAt(null);

            entry.setHodRemarks(null);
        }


        timetableRepository.saveAll(entries);
    }


    // ============================================================
    // FACULTY TIMETABLE
    // ============================================================

    @Override
    public List<FacultyTimetableResponse> getFacultyTimetable(
            Long facultyId
    ) {

        if (!facultyRepository.existsById(facultyId)) {

            throw new ResourceNotFoundException(
                    "Faculty Not Found"
            );
        }

        return timetableRepository
                .findFacultyTimetable(facultyId)
                .stream()
                .map(this::mapFacultyTimetable)
                .toList();
    }


    private FacultyTimetableResponse mapFacultyTimetable(
            TimetableEntry entry
    ) {

        return FacultyTimetableResponse.builder()

                .day(
                        entry.getDay().name()
                )

                .period(
                        entry.getPeriodNumber()
                )

                .subject(
                        entry.getSubject()
                                .getSubjectName()
                )

                .branch(
                        entry.getStudentClass()
                                .getBranch()
                                .getName()
                )

                .year(
                        entry.getStudentClass()
                                .getYear()
                )

                .semester(
                        entry.getStudentClass()
                                .getSemester()
                )

                .section(
                        entry.getStudentClass()
                                .getSection()
                )

                .room(
                        entry.getRoom()
                                .getRoomNumber()
                )

                .build();
    }


    // ============================================================
    // ADMIN APPROVAL USING REQUEST
    // ============================================================

    @Override
    @Transactional
    public TimetableApprovalResponse approveTimetable(
            ApproveTimetableRequest request
    ) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(
                        request.getAcademicYearId()
                );


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (entries.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No timetable found"
            );
        }


        LocalDateTime now =
                LocalDateTime.now();


        for (TimetableEntry entry : entries) {

            entry.setApproved(
                    request.getApproved()
            );


            if (Boolean.TRUE.equals(
                    request.getApproved()
            )) {

                entry.setApprovedAt(now);

            } else {

                entry.setApprovedAt(null);
            }


            entry.setRemarks(
                    request.getRemarks()
            );
        }


        timetableRepository.saveAll(entries);


        return TimetableApprovalResponse.builder()

                .studentClassId(
                        studentClass.getId()
                )

                .academicYearId(
                        academicYear.getId()
                )

                .approved(
                        request.getApproved()
                )

                .remarks(
                        request.getRemarks()
                )

                .approvedEntries(
                        entries.size()
                )

                .approvedAt(
                        Boolean.TRUE.equals(
                                request.getApproved()
                        )
                                ? now
                                : null
                )

                .build();
    }


    // ============================================================
    // VALIDATE TIMETABLE
    // ============================================================

    @Override
    public TimetableValidationResponse validateTimetable(
            Long studentClassId,
            Long academicYearId
    ) {

        StudentClass studentClass =
                getStudentClass(studentClassId);

        validateDepartmentAccess(studentClass);

        AcademicYear academicYear =
                getAcademicYear(academicYearId);


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (entries.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No timetable found"
            );
        }


        int facultyConflicts = 0;

        int roomConflicts = 0;

        int classConflicts = 0;

        int missingHours = 0;

        int extraHours = 0;


        // ========================================================
        // FACULTY CONFLICTS
        // ========================================================

        for (TimetableEntry entry : entries) {

            long count =
                    timetableRepository
                            .findFacultyTimetable(
                                    entry.getFaculty().getId()
                            )
                            .stream()

                            .filter(e ->
                                    e.getAcademicYear()
                                            .getId()
                                            .equals(
                                                    academicYearId
                                            )
                            )

                            .filter(e ->
                                    e.getDay()
                                            == entry.getDay()
                            )

                            .filter(e ->
                                    e.getPeriodNumber()
                                            .equals(
                                                    entry.getPeriodNumber()
                                            )
                            )

                            .count();


            if (count > 1) {
                facultyConflicts++;
            }
        }


        // ========================================================
        // ROOM CONFLICTS
        // ========================================================

        for (TimetableEntry entry : entries) {

            long count =
                    timetableRepository
                            .findAll()
                            .stream()

                            .filter(e ->
                                    e.getAcademicYear()
                                            .getId()
                                            .equals(
                                                    academicYearId
                                            )
                            )

                            .filter(e ->
                                    e.getRoom()
                                            .getId()
                                            .equals(
                                                    entry.getRoom()
                                                            .getId()
                                            )
                            )

                            .filter(e ->
                                    e.getDay()
                                            == entry.getDay()
                            )

                            .filter(e ->
                                    e.getPeriodNumber()
                                            .equals(
                                                    entry.getPeriodNumber()
                                            )
                            )

                            .count();


            if (count > 1) {
                roomConflicts++;
            }
        }


        // ========================================================
        // CLASS CONFLICTS
        // ========================================================

        for (TimetableEntry entry : entries) {

            long count =
                    entries
                            .stream()

                            .filter(e ->
                                    e.getDay()
                                            == entry.getDay()
                            )

                            .filter(e ->
                                    e.getPeriodNumber()
                                            .equals(
                                                    entry.getPeriodNumber()
                                            )
                            )

                            .count();


            if (count > 1) {
                classConflicts++;
            }
        }


        // ========================================================
        // SUBJECT HOURS
        // ========================================================

        List<FacultyAssignment> assignments =
                facultyAssignmentRepository
                        .findByStudentClass(
                                studentClass
                        );


        for (FacultyAssignment assignment :
                assignments) {

            Subject subject =
                    assignment
                            .getCurriculumSubject()
                            .getSubject();


            // Use the same weekly-hours resolution the generator uses
            // (WorkloadBuilder): curriculum-level override if present,
            // otherwise the subject's hoursPerWeek, falling back to
            // totalHours only for legacy rows with neither set.
            // Using subject.getTotalHours() here (the semester total,
            // e.g. theoryHours + labHours) instead of the weekly
            // scheduling hours is what previously caused
            // "Missing Hours" to be wildly overreported even when the
            // timetable was fully and correctly allocated.
            Integer resolvedWeeklyHours =
                    assignment
                            .getCurriculumSubject()
                            .resolveHoursPerWeek();

            int requiredHours =
                    resolvedWeeklyHours != null
                            ? resolvedWeeklyHours
                            : subject.getTotalHours();


            long allocatedHours =
                    entries
                            .stream()
                            .filter(e ->
                                    e.getSubject()
                                            .getId()
                                            .equals(
                                                    subject.getId()
                                            )
                            )
                            .count();


            if (allocatedHours < requiredHours) {

                missingHours +=
                        (int) (
                                requiredHours
                                        - allocatedHours
                        );
            }


            if (allocatedHours > requiredHours) {

                extraHours +=
                        (int) (
                                allocatedHours
                                        - requiredHours
                        );
            }
        }


        boolean valid =
                facultyConflicts == 0
                        && roomConflicts == 0
                        && classConflicts == 0
                        && missingHours == 0
                        && extraHours == 0;


        return TimetableValidationResponse.builder()

                .facultyConflicts(
                        facultyConflicts
                )

                .roomConflicts(
                        roomConflicts
                )

                .classConflicts(
                        classConflicts
                )

                .missingHours(
                        missingHours
                )

                .extraHours(
                        extraHours
                )

                .valid(valid)

                .build();
    }


    // ============================================================
    // APPROVE TIMETABLE BY CLASS
    // ============================================================

    @Override
    @Transactional
    public void approveTimetable(
            Long classId,
            String remarks
    ) {

        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassIdAndStatus(
                                classId,
                                TimetableStatus.DRAFT
                        );


        if (entries.isEmpty()) {

            throw new IllegalStateException(
                    "No pending timetable found."
            );
        }


        for (TimetableEntry entry : entries) {

            entry.setApproved(true);

            entry.setApprovedAt(
                    LocalDateTime.now()
            );

            entry.setRemarks(remarks);

            entry.setStatus(
                    TimetableStatus.APPROVED
            );
        }


        timetableRepository.saveAll(entries);
    }


    // ============================================================
    // REJECT TIMETABLE BY CLASS
    // ============================================================

    @Override
    @Transactional
    public void rejectTimetable(
            Long classId,
            String remarks
    ) {

        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassIdAndStatus(
                                classId,
                                TimetableStatus.DRAFT
                        );


        if (entries.isEmpty()) {

            throw new IllegalStateException(
                    "No pending timetable found."
            );
        }


        for (TimetableEntry entry : entries) {

            entry.setApproved(false);

            entry.setApprovedAt(null);

            entry.setRemarks(remarks);

            entry.setStatus(
                    TimetableStatus.REJECTED
            );
        }


        timetableRepository.saveAll(entries);
    }


    // ============================================================
    // REGENERATE TIMETABLE
    // ============================================================

    @Override
    @Transactional
    public void regenerateTimetable(
            Long classId
    ) {

        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassId(classId);


        if (entries.isEmpty()) {

            throw new IllegalStateException(
                    "No timetable found."
            );
        }


        /*
         * Store academic year BEFORE deleting.
         */
        Long academicYearId =
                entries.get(0)
                        .getAcademicYear()
                        .getId();


        /*
         * Do not regenerate approved timetable.
         */
        if (entries.stream()
                .anyMatch(TimetableEntry::getApproved)) {

            throw new IllegalStateException(
                    "Approved timetable cannot be regenerated."
            );
        }


        /*
         * Delete old timetable.
         */
        timetableRepository.deleteAll(entries);

        /*
         * TimetableEntry uses GenerationType.IDENTITY, so the INSERTs
         * inside generateTimetable() below execute immediately -
         * Hibernate has no choice, it needs the DB-generated id right
         * away. The DELETE above, however, is queued and would
         * normally only be flushed at the end of the transaction.
         *
         * regenerateTimetable() calls generateTimetable() directly
         * (a plain method call on `this`, not through the Spring
         * proxy), so both run inside the SAME transaction. Without
         * this explicit flush, the queued DELETE from this method and
         * the INSERTs inside generateTimetable()'s saveAll() can race:
         * a new row for e.g. (studentClass=9, academicYear=1,
         * WEDNESDAY, period=2) gets inserted while the OLD row at that
         * exact same unique key is still physically present in the
         * table, causing a raw DataIntegrityViolationException instead
         * of a clean regenerate.
         */
        timetableRepository.flush();


        /*
         * Reload student class.
         */
        StudentClass studentClass =
                studentClassRepository
                        .findById(classId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found."
                                )
                        );


        validateDepartmentAccess(studentClass);


        GenerateTimetableRequest request =
                new GenerateTimetableRequest();


        request.setStudentClassId(
                studentClass.getId()
        );


        request.setAcademicYearId(
                academicYearId
        );


        generateTimetable(request);
    }

    // ============================================================
    // CLASS TIMETABLE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getClassTimetable(
            Long classId
    ) {

        StudentClass studentClass =
                getStudentClass(classId);

        validateDepartmentAccess(studentClass);


        return timetableRepository
                .findClassTimetable(classId)
                .stream()
                .map(this::map)
                .toList();
    }


    // ============================================================
    // ROOM TIMETABLE
    // ============================================================

    @Override
    public List<TimetableResponse> getRoomTimetable(
            Long roomId
    ) {

        return timetableRepository
                .findByRoomIdOrderByDayAscPeriodNumberAsc(
                        roomId
                )
                .stream()
                .map(this::map)
                .toList();
    }


    // ============================================================
    // DAY TIMETABLE
    // ============================================================

    @Override
    public List<TimetableResponse> getDayTimetable(
            WorkingDay day
    ) {

        return timetableRepository
                .findByDayOrderByPeriodNumberAsc(day)
                .stream()
                .map(this::map)
                .toList();
    }


    // ============================================================
    // DEPARTMENT TIMETABLE
    // ============================================================

    @Override
    public List<TimetableResponse> getDepartmentTimetable(
            Long branchId
    ) {

        return timetableRepository
                .findByStudentClassBranchIdOrderByDayAscPeriodNumberAsc(
                        branchId
                )
                .stream()
                .map(this::map)
                .toList();
    }


    // ============================================================
    // PENDING TIMETABLES FOR HOD
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getPendingTimetables() {

        User currentUser =
                currentUserService.getCurrentUser();


        if (currentUser.getBranch() == null) {

            throw new ResourceNotFoundException(
                    "HOD is not assigned to a department."
            );
        }


        Long branchId =
                currentUser.getBranch().getId();


        return timetableRepository
                .findByStatusOrderByStudentClassAsc(
                        TimetableStatus.SUBMITTED
                )
                .stream()

                .filter(entry ->
                        entry.getStudentClass()
                                .getBranch()
                                .getId()
                                .equals(branchId)
                )

                .map(this::map)

                .toList();
    }

    // ============================================================
    // HOD APPROVE
    // ============================================================

    @Override
    @Transactional
    public void hodApprove(
            HODApprovalRequest request
    ) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );


        validateDepartmentAccess(studentClass);


        AcademicYear academicYear =
                getAcademicYear(
                        request.getAcademicYearId()
                );


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (entries.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No timetable found"
            );
        }


        User currentUser =
                currentUserService.getCurrentUser();


        LocalDateTime now =
                LocalDateTime.now();


        for (TimetableEntry entry : entries) {

            entry.setStatus(
                    TimetableStatus.APPROVED
            );

            entry.setApproved(true);

            entry.setApprovedAt(now);

            entry.setApprovedByHod(
                    currentUser
            );

            entry.setHodApprovedAt(now);

            entry.setHodDecisionAt(now);

            entry.setHodRemarks(
                    request.getRemarks()
            );
        }


        timetableRepository.saveAll(entries);

        notifyAdminsOfHodDecision(
                studentClass,
                academicYear,
                TimetableStatus.APPROVED,
                request.getRemarks()
        );
    }


    // ============================================================
    // HOD REJECT
    // ============================================================

    @Override
    @Transactional
    public void hodReject(
            HODApprovalRequest request
    ) {

        StudentClass studentClass =
                getStudentClass(
                        request.getStudentClassId()
                );


        validateDepartmentAccess(studentClass);


        AcademicYear academicYear =
                getAcademicYear(
                        request.getAcademicYearId()
                );


        List<TimetableEntry> entries =
                timetableRepository
                        .findByStudentClassAndAcademicYear(
                                studentClass,
                                academicYear
                        );


        if (entries.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No timetable found"
            );
        }


        LocalDateTime now =
                LocalDateTime.now();


        for (TimetableEntry entry : entries) {

            entry.setStatus(
                    TimetableStatus.REJECTED
            );

            entry.setApproved(false);

            entry.setApprovedAt(null);

            entry.setApprovedByHod(null);

            entry.setHodApprovedAt(null);

            entry.setHodDecisionAt(now);

            entry.setHodRemarks(
                    request.getRemarks()
            );
        }


        timetableRepository.saveAll(entries);

        notifyAdminsOfHodDecision(
                studentClass,
                academicYear,
                TimetableStatus.REJECTED,
                request.getRemarks()
        );
    }


    // ============================================================
    // NOTIFY DEPARTMENT ADMIN(S) OF HOD DECISION
    // ============================================================

    /**
     * Notifies every ADMIN user in the timetable's department that
     * the HOD has approved/rejected it. Failing to find an admin
     * (e.g. branch has none configured yet) is not an error - the
     * approve/reject action itself already succeeded and must not be
     * rolled back just because there's no one to notify.
     */
    private void notifyAdminsOfHodDecision(
            StudentClass studentClass,
            AcademicYear academicYear,
            TimetableStatus status,
            String remarks
    ) {

        Long branchId =
                studentClass.getBranch().getId();

        List<User> admins =
                userRepository.findByBranchIdAndRole(
                        branchId,
                        Role.ADMIN
                );

        if (admins.isEmpty()) {
            return;
        }

        boolean approved =
                status == TimetableStatus.APPROVED;

        String className =
                studentClass.getBranch().getName()
                        + " " + studentClass.getYear()
                        + "-" + studentClass.getSection();

        String title =
                approved
                        ? "Timetable Approved"
                        : "Timetable Rejected";

        String message =
                "Timetable for " + className
                        + " (" + academicYear.getName() + ") was "
                        + (approved ? "approved" : "rejected")
                        + " by the HOD"
                        + (remarks != null && !remarks.isBlank()
                        ? ". Remarks: " + remarks
                        : ".");

        NotificationType type =
                approved
                        ? NotificationType.TIMETABLE_APPROVED
                        : NotificationType.TIMETABLE_REJECTED;

        for (User admin : admins) {

            notificationService.notify(
                    admin,
                    type,
                    title,
                    message,
                    studentClass.getId(),
                    academicYear.getId(),
                    className
            );
        }
    }


    // ============================================================
    // HOD APPROVED / REJECTED TIMETABLE HISTORY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<HodTimetableHistoryResponse> getHodApprovedTimetables() {

        return getHodTimetableHistory(TimetableStatus.APPROVED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HodTimetableHistoryResponse> getHodRejectedTimetables() {

        return getHodTimetableHistory(TimetableStatus.REJECTED);
    }

    private List<HodTimetableHistoryResponse> getHodTimetableHistory(
            TimetableStatus status
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getBranch() == null) {

            throw new ResourceNotFoundException(
                    "HOD is not assigned to a department."
            );
        }

        Long branchId =
                currentUser.getBranch().getId();

        List<TimetableEntry> entries =
                timetableRepository
                        .findByStatusOrderByStudentClassAsc(status)
                        .stream()
                        .filter(entry ->
                                entry.getStudentClass()
                                        .getBranch()
                                        .getId()
                                        .equals(branchId)
                        )
                        .toList();

        /*
         * A "timetable" here is one (studentClass, academicYear) pair
         * made up of many period-level TimetableEntry rows, all
         * carrying the same HOD decision - group them back into one
         * history row per timetable instead of one per period.
         */
        Map<String, List<TimetableEntry>> grouped =
                entries.stream()
                        .collect(
                                Collectors.groupingBy(entry ->
                                        entry.getStudentClass().getId()
                                                + "-"
                                                + entry.getAcademicYear().getId()
                                )
                        );

        return grouped.values()
                .stream()
                .map(this::mapHistory)
                .sorted(
                        Comparator.comparing(
                                HodTimetableHistoryResponse::getDecidedAt,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                )
                .toList();
    }

    private HodTimetableHistoryResponse mapHistory(
            List<TimetableEntry> group
    ) {

        TimetableEntry first = group.get(0);

        return HodTimetableHistoryResponse.builder()

                .studentClassId(
                        first.getStudentClass().getId()
                )

                .academicYearId(
                        first.getAcademicYear().getId()
                )

                .className(
                        first.getStudentClass()
                                .getBranch()
                                .getName()
                                + " " + first.getStudentClass().getYear()
                                + "-" + first.getStudentClass().getSection()
                )

                .academicYear(
                        first.getAcademicYear().getName()
                )

                .status(
                        first.getStatus().name()
                )

                .remarks(
                        first.getHodRemarks()
                )

                .decidedAt(
                        first.getHodDecisionAt()
                )

                .periodCount(
                        group.size()
                )

                .build();
    }


    // ============================================================
    // RESPONSE MAPPER
    // ============================================================

    private TimetableResponse map(
            TimetableEntry entry
    ) {

        return TimetableResponse.builder()

                .id(
                        entry.getId()
                )

                .studentClassId(
                        entry.getStudentClass()
                                .getId()
                )

                .academicYearId(
                        entry.getAcademicYear()
                                .getId()
                )

                .className(
                        entry.getStudentClass()
                                .getBranch()
                                .getName()
                                + " "
                                + entry.getStudentClass()
                                .getYear()
                                + "-"
                                + entry.getStudentClass()
                                .getSection()
                )

                .subject(
                        entry.getSubject()
                                .getSubjectName()
                )

                .faculty(
                        entry.getFaculty()
                                .getName()
                )

                .room(
                        entry.getRoom()
                                .getRoomNumber()
                )

                .day(
                        entry.getDay()
                                .name()
                )

                .periodNumber(
                        entry.getPeriodNumber()
                )

                .academicYear(
                        entry.getAcademicYear()
                                .getName()
                )

                .approved(
                        entry.getApproved()
                )

                .build();
    }
}