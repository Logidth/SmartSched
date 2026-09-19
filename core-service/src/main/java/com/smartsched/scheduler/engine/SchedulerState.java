package com.smartsched.scheduler.engine;

import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tracks scheduler occupancy and exposes the queries the candidate
 * generator / scorer run for every slot they consider.
 *
 * PERFORMANCE NOTE
 * -----------------
 * The previous implementation kept a single flat List<TimetableEntry>
 * and answered every query (getSubjectCount, getGapCount,
 * hasAdjacentSubject, daysSinceLastSubject, ...) by looping over ALL
 * entries placed so far. Those queries are called from
 * CandidateGenerator / ScheduleScorer once per candidate (day,
 * period) pair, for every workload, every round - so the full
 * generation run was effectively O(entries x candidates x rounds),
 * and got quadratic-or-worse as a timetable filled up.
 *
 * This version keeps small per-class / per-faculty / per-subject
 * indexes (HashMaps of TreeSets) that are updated incrementally in
 * occupy(), so every one of the queries below is O(1) or O(log k)
 * where k is the tiny number of periods in a day / days in a week -
 * never proportional to the total number of entries in the
 * timetable. The public API is unchanged, so this is a drop-in
 * replacement for every existing caller.
 *
 * NOTE: an earlier version of this class also kept a flat
 * List<TimetableEntry> `entries` field, appended to in occupy() and
 * removed from in release(). Nothing outside this class ever read
 * it (no getter was ever exposed), so it was pure dead weight - EXCEPT
 * release() used List.remove(Object) to drop a single entry from it,
 * which is a linear scan (TimetableEntry has no equals()/hashCode()
 * override, so it fell back to reference equality). loadExisting()
 * seeds occupancy from every timetable entry in the whole academic
 * year across every class/branch, so that list could easily hold
 * thousands of entries. ConstraintPropagationSchedulerEngine's
 * backtracking search calls occupy()/release() on the order of
 * hundreds of thousands to millions of times per generate() call -
 * so every one of those "O(1)" releases was secretly an O(existing
 * entries in the academic year) scan, which is what made timetable
 * generation appear to hang instead of completing. The field has
 * been removed entirely since it had no real consumer.
 */
public class SchedulerState {

    // raw occupancy (unchanged from before)
    private final Set<String> classSlots = new HashSet<>();
    private final Set<String> facultySlots = new HashSet<>();
    private final Set<String> roomSlots = new HashSet<>();

    // classId -> day -> sorted set of occupied periods
    private final Map<Long, Map<WorkingDay, TreeSet<Integer>>> classDayPeriods = new HashMap<>();

    // facultyId -> day -> sorted set of occupied periods
    private final Map<Long, Map<WorkingDay, TreeSet<Integer>>> facultyDayPeriods = new HashMap<>();

    // facultyId -> total periods assigned this week
    private final Map<Long, Integer> facultyWeeklyPeriods = new HashMap<>();

    // roomId -> total periods assigned this week (used by the CSP engine
    // to prefer the least-loaded compatible room when it commits a
    // candidate, purely for spreading usage across rooms - not required
    // for correctness)
    private final Map<Long, Integer> roomWeeklyPeriods = new HashMap<>();

    // classId -> subjectId -> day -> sorted set of occupied periods
    private final Map<Long, Map<Long, Map<WorkingDay, TreeSet<Integer>>>> classSubjectDayPeriods = new HashMap<>();

    // classId -> subjectId -> sorted set of day ordinals the subject was taught on
    private final Map<Long, Map<Long, TreeSet<Integer>>> classSubjectDays = new HashMap<>();

    private String key(Long id, WorkingDay day, int period) {
        return id + "_" + day + "_" + period;
    }

    public void loadExisting(List<TimetableEntry> existingEntries) {

        classSlots.clear();
        facultySlots.clear();
        roomSlots.clear();

        classDayPeriods.clear();
        facultyDayPeriods.clear();
        facultyWeeklyPeriods.clear();
        roomWeeklyPeriods.clear();
        classSubjectDayPeriods.clear();
        classSubjectDays.clear();

        for (TimetableEntry entry : existingEntries) {
            occupy(entry);
        }
    }

    public boolean isClassFree(Long classId, WorkingDay day, int period) {
        return !classSlots.contains(key(classId, day, period));
    }

    public boolean isFacultyFree(Long facultyId, WorkingDay day, int period) {
        return !facultySlots.contains(key(facultyId, day, period));
    }

    public boolean isRoomFree(Long roomId, WorkingDay day, int period) {
        return !roomSlots.contains(key(roomId, day, period));
    }

    /**
     * Inverse of occupy(): removes exactly this one entry's effect on the
     * indexes, in O(1)/O(log k), without touching anything else that was
     * already in the state before it (e.g. other classes' bookings).
     *
     * This exists specifically so backtracking search (see
     * ConstraintPropagationSchedulerEngine) can undo a single tentative
     * placement and keep going, instead of calling loadExisting() with a
     * partial entry list - which would silently discard every entry NOT
     * in that list, including bookings that belong to other classes and
     * were never part of this search to begin with.
     */
    public void release(TimetableEntry entry) {

        Long classId = entry.getStudentClass().getId();
        Long facultyId = entry.getFaculty().getId();
        Long roomId = entry.getRoom().getId();
        Long subjectId = entry.getSubject().getId();
        WorkingDay day = entry.getDay();
        int period = entry.getPeriodNumber();

        classSlots.remove(key(classId, day, period));
        facultySlots.remove(key(facultyId, day, period));
        roomSlots.remove(key(roomId, day, period));

        removeFromIndex(classDayPeriods, classId, day, period);
        removeFromIndex(facultyDayPeriods, facultyId, day, period);

        facultyWeeklyPeriods.computeIfPresent(facultyId, (id, count) -> count <= 1 ? null : count - 1);
        roomWeeklyPeriods.computeIfPresent(roomId, (id, count) -> count <= 1 ? null : count - 1);

        Map<Long, Map<WorkingDay, TreeSet<Integer>>> bySubject = classSubjectDayPeriods.get(classId);
        if (bySubject != null) {
            Map<WorkingDay, TreeSet<Integer>> byDay = bySubject.get(subjectId);
            if (byDay != null) {
                TreeSet<Integer> periods = byDay.get(day);
                if (periods != null) {
                    periods.remove(period);
                    if (periods.isEmpty()) {
                        byDay.remove(day);

                        // Only drop the day from classSubjectDays once no
                        // periods of that subject remain on that day.
                        Map<Long, TreeSet<Integer>> subjectDays = classSubjectDays.get(classId);
                        if (subjectDays != null) {
                            TreeSet<Integer> days = subjectDays.get(subjectId);
                            if (days != null) {
                                days.remove(day.ordinal());
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeFromIndex(
            Map<Long, Map<WorkingDay, TreeSet<Integer>>> index, Long id, WorkingDay day, int period
    ) {
        Map<WorkingDay, TreeSet<Integer>> byDay = index.get(id);
        if (byDay == null) return;

        TreeSet<Integer> periods = byDay.get(day);
        if (periods == null) return;

        periods.remove(period);
        if (periods.isEmpty()) {
            byDay.remove(day);
        }
    }

    public void occupy(TimetableEntry entry) {

        Long classId = entry.getStudentClass().getId();
        Long facultyId = entry.getFaculty().getId();
        Long roomId = entry.getRoom().getId();
        Long subjectId = entry.getSubject().getId();
        WorkingDay day = entry.getDay();
        int period = entry.getPeriodNumber();

        classSlots.add(key(classId, day, period));
        facultySlots.add(key(facultyId, day, period));
        roomSlots.add(key(roomId, day, period));

        classDayPeriods
                .computeIfAbsent(classId, k -> new EnumMap<>(WorkingDay.class))
                .computeIfAbsent(day, k -> new TreeSet<>())
                .add(period);

        facultyDayPeriods
                .computeIfAbsent(facultyId, k -> new EnumMap<>(WorkingDay.class))
                .computeIfAbsent(day, k -> new TreeSet<>())
                .add(period);

        facultyWeeklyPeriods.merge(facultyId, 1, Integer::sum);
        roomWeeklyPeriods.merge(roomId, 1, Integer::sum);

        classSubjectDayPeriods
                .computeIfAbsent(classId, k -> new HashMap<>())
                .computeIfAbsent(subjectId, k -> new EnumMap<>(WorkingDay.class))
                .computeIfAbsent(day, k -> new TreeSet<>())
                .add(period);

        classSubjectDays
                .computeIfAbsent(classId, k -> new HashMap<>())
                .computeIfAbsent(subjectId, k -> new TreeSet<>())
                .add(day.ordinal());
    }

    /** O(1) - was O(n). */
    public int getSubjectCount(Long classId, Long subjectId, WorkingDay day) {

        Map<Long, Map<WorkingDay, TreeSet<Integer>>> bySubject = classSubjectDayPeriods.get(classId);
        if (bySubject == null) return 0;

        Map<WorkingDay, TreeSet<Integer>> byDay = bySubject.get(subjectId);
        if (byDay == null) return 0;

        TreeSet<Integer> periods = byDay.get(day);
        return periods == null ? 0 : periods.size();
    }

    /** O(1) - was O(n). */
    public int getClassPeriodsForDay(Long classId, WorkingDay day) {

        Map<WorkingDay, TreeSet<Integer>> byDay = classDayPeriods.get(classId);
        if (byDay == null) return 0;

        TreeSet<Integer> periods = byDay.get(day);
        return periods == null ? 0 : periods.size();
    }

    /** O(log k) - was O(n). Only looks at periods adjacent to `period`. */
    public boolean hasAdjacentSubject(Long classId, Long subjectId, WorkingDay day, int period) {

        Map<Long, Map<WorkingDay, TreeSet<Integer>>> bySubject = classSubjectDayPeriods.get(classId);
        if (bySubject == null) return false;

        Map<WorkingDay, TreeSet<Integer>> byDay = bySubject.get(subjectId);
        if (byDay == null) return false;

        TreeSet<Integer> periods = byDay.get(day);
        if (periods == null) return false;

        return periods.contains(period - 1) || periods.contains(period + 1);
    }

    /** O(1) - was O(n). */
    public int getFacultyPeriodsForDay(Long facultyId, WorkingDay day) {

        Map<WorkingDay, TreeSet<Integer>> byDay = facultyDayPeriods.get(facultyId);
        if (byDay == null) return 0;

        TreeSet<Integer> periods = byDay.get(day);
        return periods == null ? 0 : periods.size();
    }

    /** O(log k) - was O(n). */
    public boolean hasAdjacentFacultyPeriod(Long facultyId, WorkingDay day, int period) {

        Map<WorkingDay, TreeSet<Integer>> byDay = facultyDayPeriods.get(facultyId);
        if (byDay == null) return false;

        TreeSet<Integer> periods = byDay.get(day);
        if (periods == null) return false;

        return periods.contains(period - 1) || periods.contains(period + 1);
    }

    /** O(1) - was O(n). */
    public int getFacultyWeeklyPeriods(Long facultyId) {
        return facultyWeeklyPeriods.getOrDefault(facultyId, 0);
    }

    /**
     * O(1). Used by ConstraintPropagationSchedulerEngine to prefer the
     * least-loaded compatible room when committing a candidate, so usage
     * spreads across available rooms instead of always piling onto the
     * first compatible room in the list.
     */
    public int getRoomWeeklyPeriods(Long roomId) {
        return roomWeeklyPeriods.getOrDefault(roomId, 0);
    }

    /** O(log d) - was O(n), d = number of distinct days the subject has been taught. */
    public int daysSinceLastSubject(Long classId, Long subjectId, WorkingDay day) {

        Map<Long, TreeSet<Integer>> bySubject = classSubjectDays.get(classId);
        if (bySubject == null) return 7;

        TreeSet<Integer> days = bySubject.get(subjectId);
        if (days == null || days.isEmpty()) return 7;

        int target = day.ordinal();
        int bestGap = 7;

        Integer floor = days.floor(target);
        if (floor != null) {
            bestGap = Math.min(bestGap, Math.abs(target - floor));
        }

        Integer ceiling = days.ceiling(target);
        if (ceiling != null) {
            bestGap = Math.min(bestGap, Math.abs(ceiling - target));
        }

        return bestGap;
    }

    /** O(1) - was O(n). */
    public boolean hasClassAtPeriod(Long classId, WorkingDay day, int period) {

        Map<WorkingDay, TreeSet<Integer>> byDay = classDayPeriods.get(classId);
        if (byDay == null) return false;

        TreeSet<Integer> periods = byDay.get(day);
        return periods != null && periods.contains(period);
    }

    /** O(1) - was O(n) via two hasClassAtPeriod scans. */
    public int gapPenalty(Long classId, WorkingDay day, int period) {

        int penalty = 0;

        if (hasClassAtPeriod(classId, day, period - 1)) penalty += 20;
        if (hasClassAtPeriod(classId, day, period + 1)) penalty += 20;

        return penalty;
    }

    /** O(log k) - was O(n). Only the nearest neighbours below/above `period` matter. */
    public int getGapPenalty(Long classId, WorkingDay day, int period) {

        Map<WorkingDay, TreeSet<Integer>> byDay = classDayPeriods.get(classId);
        if (byDay == null) return 0;

        TreeSet<Integer> periods = byDay.get(day);
        if (periods == null || periods.isEmpty()) return 0;

        int penalty = 0;

        Integer lower = periods.floor(period);
        if (lower != null) penalty += gapPenaltyFor(Math.abs(period - lower));

        Integer higher = periods.ceiling(period);
        if (higher != null && !higher.equals(lower)) {
            penalty += gapPenaltyFor(Math.abs(higher - period));
        }

        // Any period further away than the two immediate neighbours only
        // ever scored 15 in the original O(n) version once gap >= 3, so
        // once we've accounted for the nearest neighbour on each side the
        // remaining (farther) periods contribute the same flat 15 each.
        int remaining = periods.size() - (lower != null ? 1 : 0) - (higher != null && !higher.equals(lower) ? 1 : 0);
        penalty += remaining * 15;

        return penalty;
    }

    private int gapPenaltyFor(int gap) {
        if (gap == 1) return 0;
        if (gap == 2) return 5;
        return 15;
    }

    /** O(k) in the number of periods that day (k <= periodsPerDay) - was O(n) + a full sort. */
    public int getGapCount(Long classId, WorkingDay day) {

        Map<WorkingDay, TreeSet<Integer>> byDay = classDayPeriods.get(classId);
        if (byDay == null) return 0;

        TreeSet<Integer> periods = byDay.get(day);
        if (periods == null || periods.size() <= 1) return 0;

        int gaps = 0;
        Integer previous = null;

        for (int p : periods) {
            if (previous != null) {
                int diff = p - previous;
                if (diff > 1) {
                    gaps += diff - 1;
                }
            }
            previous = p;
        }

        return gaps;
    }
}