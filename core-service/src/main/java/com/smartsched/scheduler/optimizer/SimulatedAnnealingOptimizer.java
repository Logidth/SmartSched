package com.smartsched.scheduler.optimizer;

import com.smartsched.facultyavailability.repository.FacultyAvailabilityRepository;
import com.smartsched.scheduler.config.SchedulerConfig;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Simulated-annealing local search over an already-placed, feasible
 * timetable for a single class.
 *
 * WHY THIS EXISTS
 * ----------------
 * The greedy round-robin placement in SchedulerEngineImpl produces a
 * timetable with no hard-constraint violations, but a fairly arbitrary
 * one quality-wise (whichever of the top-5 scored candidates the RNG
 * happened to pick at placement time). The hill-climbing passes that
 * used to live in TimetableOptimizerImpl (optimizeAcrossDays /
 * optimizeBySwapping) only ever accepted a strictly-improving move, so
 * they always froze at whichever local optimum they reached first and
 * could never climb back out of it - and because every trial move
 * re-evaluated the ENTIRE timetable with TimetableEvaluator, that was
 * also expensive for what it bought.
 *
 * This class explores the same move space - relocate a single entry to
 * a different (day, period), or swap two entries' (day, period) - but,
 * like classic simulated annealing, is allowed to accept a worse move
 * with a probability that shrinks as the search "cools" (temperature
 * decays geometrically). That is precisely what lets it escape a local
 * optimum instead of freezing at the first one it finds. The search
 * always finishes by restoring the single best-scoring timetable state
 * actually observed - not wherever the random walk happened to end up -
 * so the result is never worse than a plain hill-climb would produce,
 * only ever equal or better.
 *
 * Every move is validated against the same hard constraints the rest of
 * the scheduler enforces (class/faculty/room freedom at the target
 * slot, faculty availability, no lunch period) before it is ever
 * applied, so this can never turn a feasible timetable into an
 * infeasible one. Only entries where isMovable() is true are ever
 * touched - continuous multi-period blocks (labs) must stay contiguous,
 * and nothing here knows how to move a whole block together.
 */
@Component
@RequiredArgsConstructor
public class SimulatedAnnealingOptimizer {

    private final TimetableEvaluator evaluator;
    private final SchedulerConfig schedulerConfig;
    private final FacultyAvailabilityRepository availabilityRepository;

    /*
     * Tunable annealing schedule. With these defaults the search runs
     * ~107 cooling steps * 60 moves/step =~ 6,400 candidate moves,
     * each a cheap O(entries) evaluation - milliseconds for a single
     * class's timetable (at most workingDays * periodsPerDay entries).
     */
    private static final double INITIAL_TEMPERATURE = 120.0;
    private static final double MIN_TEMPERATURE = 0.5;
    private static final double COOLING_RATE = 0.95;
    private static final int MOVES_PER_TEMPERATURE = 60;

    /*
     * How many random target slots a relocate move will try before
     * giving up for that move attempt (most random slots are already
     * occupied for a fairly full timetable, so a few retries matter).
     */
    private static final int RELOCATE_ATTEMPTS = 8;

    public void optimize(List<TimetableEntry> timetable) {

        List<TimetableEntry> movable =
                timetable.stream()
                        .filter(this::isMovable)
                        .collect(Collectors.toList());

        // Nothing meaningful to search over (0 or 1 movable entries).
        if (movable.size() < 2) {
            return;
        }

        int currentScore = evaluator.evaluate(timetable);

        int bestScore = currentScore;
        Map<TimetableEntry, WorkingDay> bestDays = snapshotDays(timetable);
        Map<TimetableEntry, Integer> bestPeriods = snapshotPeriods(timetable);

        List<WorkingDay> workingDays = schedulerConfig.getWorkingDayList();
        int periodsPerDay = schedulerConfig.getPeriodsPerDay();

        double temperature = INITIAL_TEMPERATURE;

        while (temperature > MIN_TEMPERATURE) {

            for (int i = 0; i < MOVES_PER_TEMPERATURE; i++) {

                currentScore =
                        ThreadLocalRandom.current().nextBoolean()
                                ? attemptRelocate(
                                movable,
                                timetable,
                                workingDays,
                                periodsPerDay,
                                currentScore,
                                temperature
                        )
                                : attemptSwap(
                                movable,
                                timetable,
                                currentScore,
                                temperature
                        );

                if (currentScore > bestScore) {

                    bestScore = currentScore;
                    bestDays = snapshotDays(timetable);
                    bestPeriods = snapshotPeriods(timetable);
                }
            }

            temperature *= COOLING_RATE;
        }

        // Always finish on the best timetable actually seen during the
        // search, not wherever the random walk ended up once the
        // temperature bottomed out.
        restore(timetable, bestDays, bestPeriods);

        System.out.println("--------------------------------");
        System.out.println("Simulated Annealing Optimization Finished");
        System.out.println("Best Score : " + bestScore);
        System.out.println("--------------------------------");
    }

    /**
     * Tries moving one random movable entry to a random different
     * (day, period). Applies the move only if it satisfies every hard
     * constraint, then accepts or rejects it per the simulated
     * annealing criterion. Returns the resulting current score (either
     * the improved/accepted score, or the score unchanged if nothing
     * valid was found or the move was rejected).
     */
    private int attemptRelocate(
            List<TimetableEntry> movable,
            List<TimetableEntry> timetable,
            List<WorkingDay> workingDays,
            int periodsPerDay,
            int currentScore,
            double temperature
    ) {

        TimetableEntry entry =
                movable.get(
                        ThreadLocalRandom.current()
                                .nextInt(movable.size())
                );

        WorkingDay originalDay = entry.getDay();
        int originalPeriod = entry.getPeriodNumber();

        for (int attempt = 0; attempt < RELOCATE_ATTEMPTS; attempt++) {

            WorkingDay targetDay =
                    workingDays.get(
                            ThreadLocalRandom.current()
                                    .nextInt(workingDays.size())
                    );

            int targetPeriod =
                    1 + ThreadLocalRandom.current()
                            .nextInt(periodsPerDay);

            if (targetDay == originalDay
                    && targetPeriod == originalPeriod) {
                continue;
            }

            if (isLunchPeriod(targetPeriod)) {
                continue;
            }

            if (isFacultyUnavailable(
                    entry.getFaculty().getId(),
                    targetDay,
                    targetPeriod
            )) {
                continue;
            }

            if (!isTargetSlotFree(
                    entry,
                    entry,
                    timetable,
                    targetDay,
                    targetPeriod
            )) {
                continue;
            }

            entry.setDay(targetDay);
            entry.setPeriodNumber(targetPeriod);

            int newScore = evaluator.evaluate(timetable);

            if (acceptMove(newScore - currentScore, temperature)) {
                return newScore;
            }

            entry.setDay(originalDay);
            entry.setPeriodNumber(originalPeriod);

            return currentScore;
        }

        return currentScore;
    }

    /**
     * Tries swapping the (day, period) of two random distinct movable
     * entries - unlike the old optimizeBySwapping(), this is not
     * restricted to entries on the same day. Applies the move only if
     * both resulting slots satisfy every hard constraint, then accepts
     * or rejects per the simulated annealing criterion.
     */
    private int attemptSwap(
            List<TimetableEntry> movable,
            List<TimetableEntry> timetable,
            int currentScore,
            double temperature
    ) {

        TimetableEntry first =
                movable.get(
                        ThreadLocalRandom.current()
                                .nextInt(movable.size())
                );

        TimetableEntry second;

        do {
            second =
                    movable.get(
                            ThreadLocalRandom.current()
                                    .nextInt(movable.size())
                    );
        } while (second == first);

        WorkingDay firstDay = first.getDay();
        int firstPeriod = first.getPeriodNumber();

        WorkingDay secondDay = second.getDay();
        int secondPeriod = second.getPeriodNumber();

        if (isLunchPeriod(firstPeriod)
                || isLunchPeriod(secondPeriod)) {
            return currentScore;
        }

        if (isFacultyUnavailable(
                first.getFaculty().getId(),
                secondDay,
                secondPeriod
        )) {
            return currentScore;
        }

        if (isFacultyUnavailable(
                second.getFaculty().getId(),
                firstDay,
                firstPeriod
        )) {
            return currentScore;
        }

        if (!isTargetSlotFree(
                first, second, timetable, secondDay, secondPeriod
        )) {
            return currentScore;
        }

        if (!isTargetSlotFree(
                second, first, timetable, firstDay, firstPeriod
        )) {
            return currentScore;
        }

        first.setDay(secondDay);
        first.setPeriodNumber(secondPeriod);

        second.setDay(firstDay);
        second.setPeriodNumber(firstPeriod);

        int newScore = evaluator.evaluate(timetable);

        if (acceptMove(newScore - currentScore, temperature)) {
            return newScore;
        }

        first.setDay(firstDay);
        first.setPeriodNumber(firstPeriod);

        second.setDay(secondDay);
        second.setPeriodNumber(secondPeriod);

        return currentScore;
    }

    /**
     * Standard simulated-annealing acceptance criterion: always accept
     * an improving (or equal) move; accept a worsening move with
     * probability exp(delta / temperature), which shrinks toward zero
     * both as the move gets worse and as the temperature cools. This
     * is the entire mechanism that lets the search escape a local
     * optimum instead of freezing at the first one it reaches.
     */
    private boolean acceptMove(int delta, double temperature) {

        if (delta >= 0) {
            return true;
        }

        double probability = Math.exp(delta / temperature);

        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    /**
     * True if placing/moving `moving` into (day, period) would not
     * clash with any other entry's class, faculty, or room - the same
     * three hard constraints enforced everywhere else in the
     * scheduler. `partner` is excluded from the check (used for swaps,
     * where the partner's current occupancy of that slot is exactly
     * what's being traded away).
     */
    private boolean isTargetSlotFree(
            TimetableEntry moving,
            TimetableEntry partner,
            List<TimetableEntry> timetable,
            WorkingDay day,
            int period
    ) {

        for (TimetableEntry other : timetable) {

            if (other == moving || other == partner) {
                continue;
            }

            if (other.getDay() != day) {
                continue;
            }

            if (other.getPeriodNumber() != period) {
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

    private Map<TimetableEntry, WorkingDay> snapshotDays(
            List<TimetableEntry> timetable
    ) {

        Map<TimetableEntry, WorkingDay> snapshot =
                new IdentityHashMap<>();

        for (TimetableEntry entry : timetable) {
            snapshot.put(entry, entry.getDay());
        }

        return snapshot;
    }

    private Map<TimetableEntry, Integer> snapshotPeriods(
            List<TimetableEntry> timetable
    ) {

        Map<TimetableEntry, Integer> snapshot =
                new IdentityHashMap<>();

        for (TimetableEntry entry : timetable) {
            snapshot.put(entry, entry.getPeriodNumber());
        }

        return snapshot;
    }

    private void restore(
            List<TimetableEntry> timetable,
            Map<TimetableEntry, WorkingDay> days,
            Map<TimetableEntry, Integer> periods
    ) {

        for (TimetableEntry entry : timetable) {
            entry.setDay(days.get(entry));
            entry.setPeriodNumber(periods.get(entry));
        }
    }
}