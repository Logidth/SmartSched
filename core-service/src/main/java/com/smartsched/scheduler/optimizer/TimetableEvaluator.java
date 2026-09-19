package com.smartsched.scheduler.optimizer;

import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TimetableEvaluator {

    public int evaluate(List<TimetableEntry> timetable) {

        int score = 0;

        score += scoreMorningPreference(timetable);

        score += scoreDailyBalance(timetable);

        score += scoreSubjectSpacing(timetable);

        score += scoreFacultyBalance(timetable);

        score += scorePreferredRooms(timetable);

        score += scoreLatePeriods(timetable);

        score += scoreFacultyGaps(timetable);

        score += scoreClassGaps(timetable);

        return score;
    }

    /**
     * BUGFIX: this evaluator drives every post-placement move/swap the
     * optimizer makes (optimizeLatePeriods / optimizeAcrossDays /
     * optimizeBySwapping all keep or discard a move based on whether
     * evaluate() went up). Before this method existed, evaluate() had
     * scoreFacultyGaps() (gaps in a FACULTY member's day) and
     * scoreSubjectSpacing() (gaps in the DAYS a subject is taught
     * across the week), but nothing at all scored a hole inside a
     * STUDENT CLASS's own day - e.g. class free at period 3 while
     * periods 2 and 4 are both taken. That is exactly the kind of gap
     * students see as a "hole" in their timetable. Because it was
     * unscored, the optimizer had zero incentive to ever close such a
     * hole - and could even introduce one while chasing faculty-gap or
     * room-preference points, since neither of those terms cared. This
     * method plugs that missing term in so a class-day gap is always
     * one of the most negative things on the board, which makes the
     * existing swap/move passes actually pull later periods back to
     * fill it.
     */
    private int scoreClassGaps(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        Map<String, List<Integer>> classPeriods =
                new HashMap<>();

        for (TimetableEntry entry : timetable) {

            String key =
                    entry.getStudentClass().getId()
                            + "_"
                            + entry.getDay();

            classPeriods
                    .computeIfAbsent(
                            key,
                            k -> new ArrayList<>()
                    )
                    .add(entry.getPeriodNumber());
        }

        for (List<Integer> periods : classPeriods.values()) {

            periods.sort(Integer::compareTo);

            for (int i = 1; i < periods.size(); i++) {

                int gap =
                        periods.get(i)
                                - periods.get(i - 1);

                score += classGapScore(gap);
            }
        }

        return score;
    }

    /**
     * Deliberately steeper than facultyGapScore(): a hole in a class's
     * own day is the thing students/HOD actually complain about, so it
     * must outweigh the softer preferences above (preferred room +25,
     * daily balance +30, etc.) or the optimizer could "fix" one of
     * those at the cost of reopening a class gap.
     */
    private int classGapScore(
            int gap
    ) {

        switch (gap) {

            case 1:
                return 20;

            case 2:
                return -30;

            case 3:
                return -60;

            case 4:
                return -90;

            default:
                return -120;
        }
    }

    private int scoreMorningPreference(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        for (TimetableEntry entry : timetable) {

            if (entry.getPeriodNumber() <= 3)
                score += 15;

            if (entry.getPeriodNumber() >= 6)
                score -= 10;
        }

        return score;
    }

    private int scoreDailyBalance(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        Map<String,Integer> map = new HashMap<>();

        for (TimetableEntry e : timetable) {

            String key =
                    e.getStudentClass().getId()
                            + "_"
                            + e.getDay();

            map.merge(
                    key,
                    1,
                    Integer::sum
            );
        }

        for (Integer count : map.values()) {

            if (count == 4)
                score += 30;

            else if (count == 5)
                score += 20;

            else if (count >= 6)
                score -= 40;

            else if (count <= 2)
                score -= 20;
        }

        return score;
    }

    private int scorePreferredRooms(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        for (TimetableEntry e : timetable) {

            if (e.getStudentClass()
                    .getPreferredRoom() == null)
                continue;

            if (e.getRoom()
                    .getId()
                    .equals(
                            e.getStudentClass()
                                    .getPreferredRoom()
                                    .getId()
                    )) {

                score += 25;

            }
        }

        return score;
    }

    private int scoreFacultyBalance(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        Map<String,Integer> load = new HashMap<>();

        for (TimetableEntry e : timetable) {

            String key =
                    e.getFaculty().getId()
                            + "_"
                            + e.getDay();

            load.merge(
                    key,
                    1,
                    Integer::sum
            );
        }

        for (Integer count : load.values()) {

            if (count <= 4)
                score += 20;

            if (count >= 6)
                score -= 50;

        }

        return score;
    }

    private int scoreSubjectSpacing(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        Map<String, List<WorkingDay>> map =
                new HashMap<>();

        for (TimetableEntry entry : timetable) {

            String key =
                    entry.getStudentClass().getId()
                            + "_"
                            + entry.getSubject().getId();

            map.computeIfAbsent(
                    key,
                    k -> new ArrayList<>()
            ).add(entry.getDay());
        }

        for (List<WorkingDay> days : map.values()) {

            days.sort(
                    Comparator.comparingInt(
                            Enum::ordinal
                    )
            );

            for (int i = 1; i < days.size(); i++) {

                int gap =
                        days.get(i).ordinal()
                                - days.get(i - 1).ordinal();

                score += gapScore(gap);

            }
        }

        return score;
    }

    private int gapScore(int gap) {

        switch (gap) {

            case 1:
                return -25;

            case 2:
                return 35;

            case 3:
                return 20;

            case 4:
                return 10;

            default:
                return 0;
        }

    }

    private int scoreFacultyGaps(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        Map<String, List<Integer>> facultyPeriods =
                new HashMap<>();

        for (TimetableEntry entry : timetable) {

            String key =
                    entry.getFaculty().getId()
                            + "_"
                            + entry.getDay();

            facultyPeriods
                    .computeIfAbsent(
                            key,
                            k -> new ArrayList<>()
                    )
                    .add(entry.getPeriodNumber());
        }

        for (List<Integer> periods : facultyPeriods.values()) {

            periods.sort(Integer::compareTo);

            for (int i = 1; i < periods.size(); i++) {

                int gap =
                        periods.get(i)
                                - periods.get(i - 1);

                score += facultyGapScore(gap);

            }

        }

        return score;
    }


    private int facultyGapScore(
            int gap
    ) {

        switch (gap) {

            case 1:
                return 20;

            case 2:
                return -5;

            case 3:
                return -20;

            case 4:
                return -35;

            default:
                return -50;
        }
    }

    private int scoreLatePeriods(
            List<TimetableEntry> timetable
    ) {

        int score = 0;

        for (TimetableEntry entry : timetable) {

            int period = entry.getPeriodNumber();

            // Prefer morning classes
            if (period <= 3) {
                score += 10;
            }

            // Neutral
            else if (period <= 5) {
                score += 0;
            }

            // Slight penalty
            else if (period == 6) {
                score -= 10;
            }

            // Heavy penalty
            else if (period >= 7) {
                score -= 20;
            }
        }

        return score;
    }
}