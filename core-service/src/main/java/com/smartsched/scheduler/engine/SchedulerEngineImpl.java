package com.smartsched.scheduler.engine;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.scheduler.algorithm.CandidateGenerator;
import com.smartsched.scheduler.algorithm.ScheduleCandidate;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.TimetableStatus;
import com.smartsched.scheduler.optimizer.TimetableOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Round-robin allocation engine (one workload gets one continuous
 * block placed per round, then control moves to the next workload -
 * see the comment above the round-robin loop in generate() for why).
 *
 * This is now the ONLY SchedulerEngine implementation. It replaces
 * ConstraintPropagationSchedulerEngine, which was previously wired as
 * @Primary. That engine modeled each subject's remaining hours as a
 * small number of large CSP "blocks" (e.g. one 6-hour subject became
 * a few 2-period blocks) searched with MRV + forward checking, capped
 * at MAX_BACKTRACKS=200_000 nodes across MAX_RESTARTS=5 restarts. On
 * real class sizes (many subjects/faculty competing for the same
 * 5-day x 8-period grid) that fixed budget was frequently exhausted
 * before a solution was found - even when one existed - producing
 * "search budget exhausted across 5 attempts" failures for the
 * majority of a class's subjects (seen: 80 unallocated blocks for a
 * single class). Backtracking search is fundamentally the wrong shape
 * for this problem at this scale without a much larger/adaptive
 * budget or smarter restarts, so rather than re-tuning constants that
 * only mask the failure mode, that engine has been removed and this
 * round-robin allocator (which degrades gracefully - it reports
 * exactly which remaining hours are stuck instead of discarding a
 * mostly-successful attempt) is the sole implementation again.
 */
@Primary
@Component
@RequiredArgsConstructor
public class SchedulerEngineImpl implements SchedulerEngine {

    private final CandidateGenerator candidateGenerator;

    private final WorkloadBuilder workloadBuilder;

    private final TimetableOptimizer optimizer;

    private final FacultyAssignmentRepository facultyAssignmentRepository;


    @Override
    public TimetableGenerationResult generate(
            SchedulerContext context,
            SchedulerState state
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Scheduler context cannot be null."
            );
        }

        if (context.getStudentClass() == null) {
            throw new IllegalArgumentException(
                    "Student class cannot be null."
            );
        }

        if (context.getAcademicYear() == null) {
            throw new IllegalArgumentException(
                    "Academic year cannot be null."
            );
        }

        if (context.getAssignments() == null
                || context.getAssignments().isEmpty()) {

            throw new IllegalStateException(
                    "No faculty assignments found for class "
                            + context.getStudentClass().getId()
            );
        }


        /*
         * Build subject workloads.
         *
         * WorkloadBuilder still orders lab subjects / longer blocks
         * first - that ordering only decides which workload gets
         * FIRST PICK within a round below, it no longer lets one
         * workload exhaust a shared faculty's entire weekly capacity
         * before another workload taught by the same faculty is even
         * attempted.
         */
        List<Workload> workloads =
                workloadBuilder.build(
                        context.getAssignments()
                );


        List<TimetableEntry> timetable =
                new ArrayList<>();

        TimetableGenerationResult result =
                new TimetableGenerationResult();


        /*
         * Resolve the originating FacultyAssignment and the eligible
         * faculty pool once per workload up front, instead of
         * re-deriving them on every allocation attempt. A workload
         * whose eligibility can't even be resolved (no assignment
         * found / no eligible faculty at all) fails immediately and
         * never enters the round-robin loop below.
         */
        Map<Workload, FacultyAssignment> assignmentByWorkload =
                new LinkedHashMap<>();

        Map<Workload, List<Faculty>> eligibleByWorkload =
                new LinkedHashMap<>();

        List<Workload> active = new ArrayList<>();

        for (Workload workload : workloads) {

            try {

                FacultyAssignment originalAssignment =
                        findAssignment(
                                context.getAssignments(),
                                workload
                        );

                List<Faculty> eligibleFaculties =
                        findEligibleFaculties(
                                originalAssignment
                        );

                if (eligibleFaculties.isEmpty()) {

                    throw new IllegalStateException(
                            "No eligible faculty found for "
                                    + workload.getSubject()
                                    .getSubjectCode()
                    );
                }

                assignmentByWorkload.put(
                        workload,
                        originalAssignment
                );

                eligibleByWorkload.put(
                        workload,
                        eligibleFaculties
                );

                active.add(workload);

            } catch (IllegalStateException failure) {

                result.addFailure(failure.getMessage());
            }
        }


        /*
         * -----------------------------------------------------------
         * ROUND-ROBIN ALLOCATION
         * -----------------------------------------------------------
         *
         * Previously every workload was drained completely (all of
         * its remaining hours placed) before the next workload was
         * even attempted. Since WorkloadBuilder puts lab subjects and
         * long continuous blocks first, whichever subject went first
         * for a given faculty could - and did - consume that
         * faculty's ENTIRE weekly cap and fill up their days, leaving
         * nothing for the next subject taught by the same faculty.
         * That is why classes were coming out lab-only while
         * everything else failed 100%.
         *
         * Instead, every still-active workload gets ONE continuous
         * block placed per round, then we move to the next workload.
         * This shares faculty/day/room capacity fairly across all
         * subjects competing for it, rather than letting scheduling
         * order decide an all-or-nothing outcome.
         *
         * A round that places nothing for ANY active workload means
         * every remaining workload is genuinely stuck (capacity
         * exhausted / no free slot) - at that point we stop and
         * report a failure for whatever is left, exactly as before.
         */
        boolean progress = true;

        while (progress && !active.isEmpty()) {

            progress = false;

            Iterator<Workload> iterator = active.iterator();

            while (iterator.hasNext()) {

                Workload workload = iterator.next();

                boolean placed =
                        tryAllocateOneBlock(
                                workload,
                                assignmentByWorkload.get(workload),
                                eligibleByWorkload.get(workload),
                                context,
                                timetable,
                                state
                        );

                if (placed) {
                    progress = true;
                }

                if (workload.completed()) {
                    iterator.remove();
                }
            }
        }

        /*
         * Anything still active after a round made no progress at all
         * is genuinely stuck - record it as a failure. Any hours of
         * that workload that WERE placed in earlier rounds are kept.
         */
        for (Workload workload : active) {

            result.addFailure(
                    buildAllocationFailureMessage(
                            workload,
                            eligibleByWorkload.get(workload),
                            state
                    )
            );
        }


        /*
         * Final optimization pass - runs over whatever was
         * successfully placed, even if some subjects failed.
         */
        if (!timetable.isEmpty()) {

            optimizer.optimize(
                    timetable,
                    context,
                    state
            );
        }


        /*
         * Defense-in-depth: the optimizer moves entries around purely
         * by mutating day/period on the SAME TimetableEntry objects
         * that are already inside `state` (added there via
         * state.occupy() during initial placement). Its own
         * isSlotFree()/isSlotFreeForSwap()/isSlotFreeAcrossDays()
         * checks are re-validated against the live `timetable` list on
         * every move, so this should never actually happen - but if a
         * bug in any of those checks ever lets two entries for THIS
         * class land on the same (day, period), we must not let that
         * reach the database as a raw
         * "Duplicate entry '...' for key ...UKnwxdsx1d72rglwq0d56810ebr'"
         * SQL error. Every entry here already belongs to the same
         * studentClass + academicYear (one class per generate() call),
         * so (day, period) alone identifies a class-level slot clash.
         */
        List<TimetableEntry> deduplicated =
                dropSlotCollisions(timetable, result);


        result.addEntries(deduplicated);

        return result;
    }


    /**
     * Keeps the first entry seen for each (day, period) slot and drops
     * any later entry that would collide with it, recording a failure
     * for the dropped one instead of letting a duplicate reach the
     * database and blow up as a raw SQL constraint violation.
     */
    private List<TimetableEntry> dropSlotCollisions(
            List<TimetableEntry> timetable,
            TimetableGenerationResult result
    ) {

        List<TimetableEntry> kept = new ArrayList<>();
        Set<String> seenSlots = new HashSet<>();

        for (TimetableEntry entry : timetable) {

            String slotKey =
                    entry.getDay() + "_" + entry.getPeriodNumber();

            if (!seenSlots.add(slotKey)) {

                result.addFailure(
                        "Skipped a duplicate scheduling slot for "
                                + entry.getSubject().getSubjectCode()
                                + " on " + entry.getDay()
                                + " period " + entry.getPeriodNumber()
                                + " (already occupied by another "
                                + "subject for this class). Please "
                                + "regenerate or place it manually."
                );

                continue;
            }

            kept.add(entry);
        }

        return kept;
    }


    /**
     * Attempts to place ONE continuous block for this workload (i.e.
     * one "turn" in the round-robin loop in generate()) and returns
     * whether anything was placed. Unlike the old allocateWorkload(),
     * this never loops until the workload is fully completed and
     * never throws on failure - a false return just means "nothing to
     * place for this workload right now", which is a normal outcome
     * in a given round, not necessarily a permanent failure.
     */
    private boolean tryAllocateOneBlock(
            Workload workload,
            FacultyAssignment originalAssignment,
            List<Faculty> eligibleFaculties,
            SchedulerContext context,
            List<TimetableEntry> timetable,
            SchedulerState state
    ) {

        if (workload.completed()) {
            return false;
        }

        Faculty assignedFaculty =
                originalAssignment.getFaculty();


        /*
         * Assigned faculty gets priority.
         *
         * Other eligible faculty members are
         * considered only when necessary.
         */
        List<Faculty> facultyOrder =
                new ArrayList<>(eligibleFaculties);


        facultyOrder.sort(
                Comparator
                        .comparing(
                                (Faculty faculty) ->
                                        !faculty.getId()
                                                .equals(
                                                        assignedFaculty
                                                                .getId()
                                                )
                        )

                        .thenComparing(
                                faculty ->
                                        state.getFacultyWeeklyPeriods(
                                                faculty.getId()
                                        )
                        )

                        .thenComparing(
                                Faculty::getId
                        )
        );


        for (Faculty faculty : facultyOrder) {

            int requiredPeriods =
                    Math.max(
                            1,
                            workload
                                    .getSubject()
                                    .getContinuousPeriods()
                    );


            /*
             * Remaining subject hours must be enough
             * for the complete continuous block.
             */
            if (requiredPeriods
                    > workload.getRemainingHours()) {

                requiredPeriods =
                        workload.getRemainingHours();
            }


            int weeklyLoad =
                    state.getFacultyWeeklyPeriods(
                            faculty.getId()
                    );


            /*
             * Respect faculty weekly limit.
             */
            if (weeklyLoad + requiredPeriods
                    > faculty.getMaxWeeklyHours()) {

                continue;
            }


            /*
             * Generate candidates only after
             * checking weekly workload.
             */
            List<ScheduleCandidate> candidates =
                    candidateGenerator.generateCandidates(
                            context.getStudentClass(),
                            faculty,
                            workload.getSubject(),
                            context.getAcademicYear(),
                            state
                    );


            if (candidates == null
                    || candidates.isEmpty()) {

                continue;
            }


            /*
             * Candidate cannot exceed remaining
             * subject hours.
             */
            List<ScheduleCandidate> validCandidates =
                    candidates.stream()

                            .filter(candidate ->
                                    candidate != null
                            )

                            .filter(candidate ->
                                    candidate
                                            .getRequiredPeriods()
                                            > 0
                            )

                            .filter(candidate ->
                                    candidate
                                            .getRequiredPeriods()
                                            <= workload
                                            .getRemainingHours()
                            )

                            .toList();


            if (validCandidates.isEmpty()) {
                continue;
            }


            /*
             * Candidate generator should already
             * rank candidates.
             *
             * We randomly choose among the first
             * five to avoid identical timetables.
             */
            int topCandidates =
                    Math.min(
                            5,
                            validCandidates.size()
                    );


            ScheduleCandidate candidate =
                    validCandidates.get(
                            ThreadLocalRandom.current()
                                    .nextInt(topCandidates)
                    );


            int periodsToAllocate =
                    Math.min(
                            candidate.getRequiredPeriods(),
                            workload.getRemainingHours()
                    );


            /*
             * Safety check.
             */
            if (periodsToAllocate <= 0) {
                continue;
            }


            for (int i = 0;
                 i < periodsToAllocate;
                 i++) {

                int period =
                        candidate.getPeriod() + i;


                TimetableEntry entry =
                        buildEntry(
                                context,
                                originalAssignment,
                                faculty,
                                candidate,
                                period
                        );


                timetable.add(entry);

                state.occupy(entry);

                workload.allocateOneHour();
            }


            return true;
        }


        return false;
    }


    private FacultyAssignment findAssignment(
            List<FacultyAssignment> assignments,
            Workload workload
    ) {

        return assignments.stream()

                .filter(assignment ->
                        assignment.getFaculty() != null
                )

                .filter(assignment ->
                        assignment
                                .getCurriculumSubject()
                                .getSubject()
                                .getId()
                                .equals(
                                        workload
                                                .getSubject()
                                                .getId()
                                )
                )

                .filter(assignment ->
                        assignment
                                .getFaculty()
                                .getId()
                                .equals(
                                        workload
                                                .getFaculty()
                                                .getId()
                                )
                )

                .findFirst()

                .orElseThrow(() ->
                        new IllegalStateException(
                                "Faculty assignment not found for subject "
                                        + workload
                                        .getSubject()
                                        .getSubjectCode()
                        )
                );
    }


    private List<Faculty> findEligibleFaculties(
            FacultyAssignment originalAssignment
    ) {

        Map<Long, Faculty> facultyById =
                new LinkedHashMap<>();


        /*
         * Explicitly assigned faculty always comes first.
         */
        Faculty assignedFaculty =
                originalAssignment.getFaculty();


        if (assignedFaculty != null) {

            facultyById.put(
                    assignedFaculty.getId(),
                    assignedFaculty
            );
        }


        /*
         * Other faculty already explicitly assigned
         * to this same curriculum subject are also
         * eligible.
         *
         * No branch restriction is applied.
         */
        facultyAssignmentRepository

                .findByCurriculumSubject(
                        originalAssignment
                                .getCurriculumSubject()
                )

                .stream()

                .map(FacultyAssignment::getFaculty)

                .filter(faculty ->
                        faculty != null
                                && faculty.getStatus() != null
                )

                .filter(faculty ->
                        faculty.getStatus()
                                .name()
                                .equals("ACTIVE")
                )

                .forEach(faculty ->
                        facultyById.putIfAbsent(
                                faculty.getId(),
                                faculty
                        )
                );


        return new ArrayList<>(
                facultyById.values()
        );
    }


    private String buildAllocationFailureMessage(
            Workload workload,
            List<Faculty> eligibleFaculties,
            SchedulerState state
    ) {

        String facultyLoads =
                eligibleFaculties.stream()

                        .map(faculty ->
                                faculty.getName()
                                        + "="
                                        + state
                                        .getFacultyWeeklyPeriods(
                                                faculty.getId()
                                        )
                                        + "/"
                                        + faculty
                                        .getMaxWeeklyHours()
                        )

                        .toList()
                        .toString();


        System.err.println(
                "========================================"
        );

        System.err.println(
                "SCHEDULER FAILED TO ALLOCATE"
        );

        System.err.println(
                "Class : "
                        + workload.getSubject()
                        .getSubjectCode()
        );

        System.err.println(
                "Subject : "
                        + workload.getSubject()
                        .getSubjectName()
        );

        System.err.println(
                "Remaining Hours : "
                        + workload.getRemainingHours()
        );

        System.err.println(
                "Eligible Faculty Loads : "
                        + facultyLoads
        );

        System.err.println(
                "========================================"
        );


        return "Unable to allocate remaining "
                + workload.getRemainingHours()
                + " hour(s) for "
                + workload.getSubject().getSubjectCode()
                + ". Eligible faculty loads: "
                + facultyLoads;
    }


    private TimetableEntry buildEntry(
            SchedulerContext context,
            FacultyAssignment assignment,
            Faculty faculty,
            ScheduleCandidate candidate,
            int period
    ) {

        return TimetableEntry.builder()

                .studentClass(
                        context.getStudentClass()
                )

                .academicYear(
                        context.getAcademicYear()
                )

                .faculty(
                        faculty
                )

                .subject(
                        assignment
                                .getCurriculumSubject()
                                .getSubject()
                )

                .room(
                        candidate.getRoom()
                )

                .day(
                        candidate.getDay()
                )

                .periodNumber(
                        period
                )

                .approved(false)

                .cancelled(false)

                .progressUpdated(false)

                .status(
                        TimetableStatus.DRAFT
                )

                .build();
    }
}