package com.smartsched.scheduler.engine;

public interface SchedulerEngine {

    /**
     * Generates a timetable for the given context.
     *
     * Does NOT throw when an individual subject/workload cannot be
     * placed - it keeps generating the rest and reports the gap via
     * TimetableGenerationResult.getFailures() instead, so a single
     * stuck subject can no longer wipe out an otherwise-successful run.
     */
    TimetableGenerationResult generate(
            SchedulerContext context,
            SchedulerState state
    );

}
