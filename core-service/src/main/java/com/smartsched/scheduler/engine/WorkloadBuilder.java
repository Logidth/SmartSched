package com.smartsched.scheduler.engine;

import com.smartsched.curriculumsubject.entity.CurriculumSubject;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkloadBuilder {

    /**
     * Resolves the weekly hours to schedule for a curriculum subject.
     * Falls back to totalHours only as a last resort, for legacy rows
     * created before hoursPerWeek existed and left unset.
     */
    private int resolveWeeklyHours(CurriculumSubject curriculumSubject) {

        Integer hoursPerWeek = curriculumSubject.resolveHoursPerWeek();

        if (hoursPerWeek != null) {
            return hoursPerWeek;
        }

        return curriculumSubject.getSubject().getTotalHours();
    }

    public List<Workload> build(
            List<FacultyAssignment> assignments
    ) {

        return assignments.stream()

                .filter(assignment ->
                        assignment.getFaculty() != null
                                && assignment.getCurriculumSubject() != null
                                && assignment.getCurriculumSubject().getSubject() != null
                )

                .map(assignment ->
                        Workload.builder()

                                .faculty(
                                        assignment.getFaculty()
                                )

                                .subject(
                                        assignment
                                                .getCurriculumSubject()
                                                .getSubject()
                                )

                                // Use the weekly scheduling hours
                                // (curriculum-level override if present,
                                // otherwise the subject's default),
                                // NOT the semester total (totalHours).
                                .remainingHours(
                                        resolveWeeklyHours(
                                                assignment.getCurriculumSubject()
                                        )
                                )

                                .build()
                )

                /*
                 * Scheduling priority:
                 *
                 * 1. Lab subjects first
                 * 2. Higher continuous-period requirement
                 * 3. Higher total hours
                 * 4. Subject code for deterministic ordering
                 */
                .sorted(
                        Comparator
                                .comparing(
                                        (Workload w) ->
                                                !w.getSubject()
                                                        .getRequiresLabRoom()
                                )

                                .thenComparing(
                                        (Workload w) ->
                                                w.getSubject()
                                                        .getContinuousPeriods(),
                                        Comparator.reverseOrder()
                                )

                                .thenComparing(
                                        Workload::getRemainingHours,
                                        Comparator.reverseOrder()
                                )

                                .thenComparing(
                                        w ->
                                                w.getSubject()
                                                        .getSubjectCode()
                                )
                )

                .collect(
                        Collectors.toCollection(
                                java.util.ArrayList::new
                        )
                );
    }
}