package com.smartsched.scheduler.exception;

import com.smartsched.common.exception.BusinessException;

import java.util.List;

/**
 * Raised when one or more subjects could not be scheduled.
 *
 * IMPORTANT: by the time this is thrown, whatever entries WERE
 * successfully generated have already been committed to the database
 * (see SchedulerServiceImpl). This exception exists to surface the
 * remaining gaps to the caller, not to roll back the partial result.
 */
public class TimetableGenerationException extends BusinessException {

    private final List<String> failures;

    public TimetableGenerationException(
            String message,
            List<String> failures
    ) {
        super(message);
        this.failures = failures;
    }

    public List<String> getFailures() {
        return failures;
    }
}
