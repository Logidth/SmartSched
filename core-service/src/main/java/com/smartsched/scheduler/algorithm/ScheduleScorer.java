package com.smartsched.scheduler.algorithm;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleScorer {

    public int score(
            StudentClass studentClass,
            Faculty faculty,
            Subject subject,
            Room room,
            WorkingDay day,
            Integer period,
            SchedulerState state
    ){

        int score = 100;
        score = 0;

        // Morning preference
        if (period <= 3) {
            score += 15;
        }

        // Late afternoon penalty
        if (period == 6) {
            score -= 100;
        }

        if (period == 7) {
            score -= 200;
        }



        // Friday afternoon penalty
        if (day == WorkingDay.FRIDAY && period >= 6) {
            score -= 10;
        }

        // Prefer days with fewer classes
        int occupied = state.getClassPeriodsForDay(
                studentClass.getId(),
                day
        );

        if (occupied == 0) {
            score += 50;
        }
        else if (occupied == 1) {
            score += 35;
        }
        else if (occupied == 2) {
            score += 20;
        }
        else if (occupied == 3) {
            score += 5;
        }
        else if (occupied >= 5) {
            score -= 40;
        }

        // Prefer compact timetable (fewer gaps)
        int gaps = state.getGapCount(
                studentClass.getId(),
                day
        );

        score -= gaps * 15;

        // Balance faculty workload
        score -= state.getFacultyPeriodsForDay(
                faculty.getId(),
                day
        ) * 25;
        score += state.gapPenalty(
                studentClass.getId(),
                day,
                period
        );

        score += state.daysSinceLastSubject(
                studentClass.getId(),
                subject.getId(),
                day
        ) * 8;

        // Strongly prefer the class's own classroom
        if (studentClass.getPreferredRoom() != null) {

            if (room.getId().equals(
                    studentClass.getPreferredRoom().getId())) {

                score += 150;

            } else {

                score -= 40;

            }
        }

        score -= state.getGapPenalty(
                studentClass.getId(),
                day,
                period
        );


        return score;
    }

}