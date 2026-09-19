package com.smartsched.scheduler.optimizer;

import com.smartsched.facultyavailability.repository.FacultyAvailabilityRepository;
import com.smartsched.scheduler.config.SchedulerConfig;
import com.smartsched.scheduler.engine.SchedulerContext;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableOptimizerImpl implements TimetableOptimizer {

    private final TimetableEvaluator evaluator;
    private final SchedulerConfig schedulerConfig;
    private final FacultyAvailabilityRepository availabilityRepository;

    @Override
    public void optimize(
            List<TimetableEntry> timetable,
            SchedulerContext context,
            SchedulerState state
    ) {

        optimizeLatePeriods(
                timetable,
                state
        );

        optimizeAcrossDays(
                timetable,
                state
        );

        optimizeBySwapping(
                timetable
        );
    }

    /*
     * A subject scheduled with more than one continuous period (labs,
     * mainly) occupies several adjacent TimetableEntry rows that must
     * stay adjacent. None of the single-entry move/swap passes below
     * know how to move a whole block together, so they must never touch
     * these entries - moving just one row of a lab block would scatter
     * the lab across non-contiguous periods.
     */
    private boolean isMovable(TimetableEntry entry) {
        return entry.getSubject().getContinuousPeriods() == null
                || entry.getSubject().getContinuousPeriods() <= 1;
    }

    private boolean isLunchPeriod(int period) {
        return period == schedulerConfig.getLunchPeriod();
    }

    private boolean isFacultyUnavailable(
            Long facultyId,
            WorkingDay day,
            int period
    ) {
        return availabilityRepository
                .existsByFacultyIdAndDayAndPeriodAndAvailableFalse(
                        facultyId,
                        day,
                        period
                );
    }

    private void optimizeLatePeriods(
            List<TimetableEntry> timetable,
            SchedulerState state
    ) {

        for (TimetableEntry entry : timetable) {

            if (!isMovable(entry)) {
                continue;
            }

            if (entry.getPeriodNumber() >= 6) {

                moveEarlier(
                        entry,
                        timetable,
                        state
                );

            }

        }

    }

    private void moveEarlier(
            TimetableEntry entry,
            List<TimetableEntry> timetable,
            SchedulerState state
    ) {

        int currentPeriod = entry.getPeriodNumber();

        for (int newPeriod = 1; newPeriod < currentPeriod; newPeriod++) {

            if (isLunchPeriod(newPeriod)) {
                continue;
            }

            if (isFacultyUnavailable(
                    entry.getFaculty().getId(),
                    entry.getDay(),
                    newPeriod
            )) {
                continue;
            }

            if (isSlotFree(
                    entry,
                    timetable,
                    newPeriod
            )) {

                System.out.println(
                        "[OPTIMIZER] "
                                + entry.getSubject().getSubjectCode()
                                + " "
                                + entry.getDay()
                                + " P"
                                + currentPeriod
                                + " -> P"
                                + newPeriod
                );

                entry.setPeriodNumber(newPeriod);

                break;
            }
        }
    }

    private boolean isSlotFree(
            TimetableEntry entry,
            List<TimetableEntry> timetable,
            int newPeriod
    ) {

        for (TimetableEntry other : timetable) {

            if (other == entry) {
                continue;
            }

            if (other.getDay() != entry.getDay()) {
                continue;
            }

            if (other.getPeriodNumber() != newPeriod) {
                continue;
            }

            // Same class
            if (other.getStudentClass().getId().equals(
                    entry.getStudentClass().getId())) {
                return false;
            }

            // Same faculty
            if (other.getFaculty().getId().equals(
                    entry.getFaculty().getId())) {
                return false;
            }

            // Same room
            if (other.getRoom().getId().equals(
                    entry.getRoom().getId())) {
                return false;
            }
        }

        return true;
    }

    private void optimizeBySwapping(
            List<TimetableEntry> timetable
    ) {

        boolean improved = true;

        int improvements = 0;

        while (improved) {

            improved = false;

            for (int i = 0; i < timetable.size(); i++) {

                TimetableEntry first = timetable.get(i);

                if (!isMovable(first)) {
                    continue;
                }

                for (int j = i + 1; j < timetable.size(); j++) {

                    TimetableEntry second = timetable.get(j);

                    if (!isMovable(second)) {
                        continue;
                    }

                    // Only swap lectures on the same day
                    if (first.getDay() != second.getDay()) {
                        continue;
                    }

                    // Check if swap is valid
                    if (!canSwap(first, second, timetable)) {
                        continue;
                    }

                    int scoreBefore = evaluator.evaluate(timetable);

                    int firstPeriod = first.getPeriodNumber();
                    int secondPeriod = second.getPeriodNumber();

                    // Perform swap
                    first.setPeriodNumber(secondPeriod);
                    second.setPeriodNumber(firstPeriod);

                    int scoreAfter = evaluator.evaluate(timetable);

                    if (scoreAfter > scoreBefore) {

                        improvements++;
                        improved = true;

                        System.out.println(
                                "[OPTIMIZER] Improved "
                                        + scoreBefore
                                        + " -> "
                                        + scoreAfter
                                        + " | "
                                        + first.getSubject().getSubjectCode()
                                        + " <-> "
                                        + second.getSubject().getSubjectCode()
                        );

                    } else {

                        // Revert swap
                        first.setPeriodNumber(firstPeriod);
                        second.setPeriodNumber(secondPeriod);
                    }
                }
            }
        }

        System.out.println("--------------------------------");
        System.out.println("Optimization Finished");
        System.out.println("Total Improvements : " + improvements);
        System.out.println("Final Score         : " + evaluator.evaluate(timetable));
        System.out.println("--------------------------------");
    }

    private boolean canSwap(
            TimetableEntry first,
            TimetableEntry second,
            List<TimetableEntry> timetable
    ) {

        if (first.getDay() != second.getDay())
            return false;

        if (first.getFaculty().getId().equals(second.getFaculty().getId()))
            return false;

        if (first.getStudentClass().getId().equals(second.getStudentClass().getId()))
            return false;

        if (isLunchPeriod(second.getPeriodNumber())
                || isLunchPeriod(first.getPeriodNumber())) {
            return false;
        }

        if (isFacultyUnavailable(
                first.getFaculty().getId(),
                second.getDay(),
                second.getPeriodNumber()
        )) {
            return false;
        }

        if (isFacultyUnavailable(
                second.getFaculty().getId(),
                first.getDay(),
                first.getPeriodNumber()
        )) {
            return false;
        }

        return isSlotFreeForSwap(
                first,
                second,
                second.getPeriodNumber(),
                timetable
        )
                &&
                isSlotFreeForSwap(
                        second,
                        first,
                        first.getPeriodNumber(),
                        timetable
                );
    }

    private boolean isSlotFreeForSwap(
            TimetableEntry moving,
            TimetableEntry swappingWith,
            int newPeriod,
            List<TimetableEntry> timetable
    ) {

        for (TimetableEntry other : timetable) {

            if (other == moving || other == swappingWith) {
                continue;
            }

            if (other.getDay() != moving.getDay()) {
                continue;
            }

            if (other.getPeriodNumber() != newPeriod) {
                continue;
            }

            if (other.getStudentClass().getId().equals(
                    moving.getStudentClass().getId())) {
                return false;
            }

            if (other.getFaculty().getId().equals(
                    moving.getFaculty().getId())) {
                return false;
            }

            if (other.getRoom().getId().equals(
                    moving.getRoom().getId())) {
                return false;
            }
        }

        return true;
    }


    private void optimizeAcrossDays(
            List<TimetableEntry> timetable,
            SchedulerState state
    ) {

        boolean improved = true;

        List<WorkingDay> workingDays =
                schedulerConfig.getWorkingDayList();

        int periodsPerDay =
                schedulerConfig.getPeriodsPerDay();

        while (improved) {

            improved = false;

            for (TimetableEntry entry : timetable) {

                if (!isMovable(entry)) {
                    continue;
                }

                int before =
                        evaluator.evaluate(timetable);

                WorkingDay originalDay =
                        entry.getDay();

                int originalPeriod =
                        entry.getPeriodNumber();

                for (WorkingDay day : workingDays) {

                    if (day == originalDay)
                        continue;

                    for (int period = 1; period <= periodsPerDay; period++) {

                        if (isLunchPeriod(period)) {
                            continue;
                        }

                        if (isFacultyUnavailable(
                                entry.getFaculty().getId(),
                                day,
                                period
                        )) {
                            continue;
                        }

                        if (!isSlotFreeAcrossDays(
                                entry,
                                timetable,
                                day,
                                period
                        )) {
                            continue;
                        }

                        entry.setDay(day);
                        entry.setPeriodNumber(period);

                        int after =
                                evaluator.evaluate(timetable);

                        if (after > before) {

                            System.out.println(
                                    "[MOVE] "
                                            + entry.getSubject().getSubjectCode()
                                            + " "
                                            + originalDay
                                            + " P"
                                            + originalPeriod
                                            + " -> "
                                            + day
                                            + " P"
                                            + period
                            );

                            improved = true;

                            before = after;

                            originalDay = day;
                            originalPeriod = period;

                        } else {

                            entry.setDay(originalDay);
                            entry.setPeriodNumber(originalPeriod);
                        }
                    }
                }
            }
        }
    }


    private boolean isSlotFreeAcrossDays(
            TimetableEntry moving,
            List<TimetableEntry> timetable,
            WorkingDay day,
            int period
    ) {

        for (TimetableEntry other : timetable) {

            if (other == moving)
                continue;

            if (other.getDay() != day)
                continue;

            if (other.getPeriodNumber() != period)
                continue;

            if (other.getStudentClass().getId().equals(
                    moving.getStudentClass().getId())) {
                return false;
            }

            if (other.getFaculty().getId().equals(
                    moving.getFaculty().getId())) {
                return false;
            }

            if (other.getRoom().getId().equals(
                    moving.getRoom().getId())) {
                return false;
            }
        }

        return true;
    }

}