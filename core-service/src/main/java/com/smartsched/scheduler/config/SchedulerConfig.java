package com.smartsched.scheduler.config;

import com.smartsched.scheduler.enums.WorkingDay;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class SchedulerConfig {

    /**
     * Number of working days.
     */
    private final int workingDays = 5;

    /**
     * Periods per day.
     *
     * Matches the 7 real teaching periods rendered in the timetable
     * grid (see TimetableGrid.jsx's PERIODS list: period 1 starts
     * 09:00, period 7 ends 04:10).
     */
    private final int periodsPerDay = 7;

    /**
     * Lunch break.
     *
     * The lunch break is NOT a numbered teaching period - it is the
     * ~50 minute gap between period 4 (ends 12:40) and period 5
     * (starts 01:30), already baked into those two periods' times in
     * the frontend. There is no period slot to skip here; 0 is a
     * sentinel meaning "no period number equals lunch", which keeps
     * every lunch-related check below a harmless no-op instead of
     * reserving (and wasting) a real teaching period.
     */
    private final int lunchPeriod = 0;
    /**
     * Maximum number of periods a single faculty member may be
     * scheduled for on any one day, ACROSS ALL CLASSES (checked
     * against the whole academic year, not just one class).
     *
     * This used to be a hardcoded "> 5" literal buried inside
     * CandidateGenerator, completely disconnected from periodsPerDay
     * (7) and from anything in the Faculty entity - Faculty only has
     * maxWeeklyHours, there has never been a per-day cap field backing
     * that "5". The practical effect was that periods 6 and 7 were
     * almost never reachable for any reasonably-loaded faculty member:
     * once they hit 5 periods somewhere that day, the remaining real
     * teaching periods were permanently off-limits to them for that
     * day, even with plenty of weekly capacity left - e.g. a faculty
     * at 10/18 weekly hours still failing to place 2 more hours
     * because every remaining day already had them at the day cap.
     *
     * Defaulting this to periodsPerDay removes that undocumented
     * extra restriction - the weekly cap (Faculty.maxWeeklyHours),
     * the adjacent-period rule, the subject-per-day limit, and the
     * room/faculty/class-free checks are what should actually govern
     * feasibility. Lower this back down if a real "don't schedule a
     * faculty member for a full day" policy is wanted - it's now a
     * single named knob instead of a magic number.
     */
    private final int maxFacultyPeriodsPerDay = periodsPerDay;
    /**
     * Single source of truth for "which days does the timetable actually
     * use". WorkingDay.values() has 6 entries (Mon-Sat); this trims it down
     * to the configured {@link #workingDays}, taken in enum declaration
     * order (Mon, Tue, ...). Every place in the scheduler that needs to
     * iterate days MUST use this instead of WorkingDay.values() directly,
     * or the workingDays setting is silently ignored.
     */
    public List<WorkingDay> getWorkingDayList() {

        WorkingDay[] all = WorkingDay.values();

        int count = Math.min(workingDays, all.length);

        return Arrays.asList(all).subList(0, count);
    }

}