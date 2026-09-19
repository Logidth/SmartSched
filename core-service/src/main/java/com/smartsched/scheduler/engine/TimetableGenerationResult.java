package com.smartsched.scheduler.engine;

import com.smartsched.scheduler.entity.TimetableEntry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Outcome of a single generate() call.
 *
 * A timetable is generated subject-by-subject. Previously, if any ONE
 * subject/workload could not be placed (e.g. every eligible faculty was
 * already at their weekly cap), the engine threw and the caller lost
 * every entry that HAD already been successfully scheduled for that
 * class - even if it was the very last subject that failed.
 *
 * This result type lets the engine keep going after a single workload
 * fails, so the caller can persist everything that succeeded and report
 * exactly which subjects still need attention (more faculty, relaxed
 * constraints, manual placement, etc.) instead of an opaque 500.
 */
@Getter
public class TimetableGenerationResult {

    private final List<TimetableEntry> entries = new ArrayList<>();

    private final List<String> failures = new ArrayList<>();

    public void addEntries(List<TimetableEntry> newEntries) {
        entries.addAll(newEntries);
    }

    public void addFailure(String message) {
        failures.add(message);
    }

    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    public boolean isFullySuccessful() {
        return failures.isEmpty();
    }

    public List<TimetableEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<String> getFailures() {
        return Collections.unmodifiableList(failures);
    }
}
