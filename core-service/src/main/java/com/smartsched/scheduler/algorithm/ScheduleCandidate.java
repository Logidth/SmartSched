
        package com.smartsched.scheduler.algorithm;

import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.enums.WorkingDay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleCandidate {

    private WorkingDay day;

    private Integer period;

    /*
     * Number of continuous periods represented
     * by this candidate.
     */
    private Integer requiredPeriods;

    private Room room;

    private int score;
}

