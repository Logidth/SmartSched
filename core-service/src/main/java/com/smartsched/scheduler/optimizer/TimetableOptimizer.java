package com.smartsched.scheduler.optimizer;

import com.smartsched.scheduler.engine.SchedulerContext;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.entity.TimetableEntry;

import java.util.List;

public interface TimetableOptimizer {

    void optimize(
            List<TimetableEntry> timetable,
            SchedulerContext context,
            SchedulerState state
    );
}