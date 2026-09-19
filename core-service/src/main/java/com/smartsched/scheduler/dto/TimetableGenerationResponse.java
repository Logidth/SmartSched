package com.smartsched.scheduler.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response for POST /generate.
 *
 * The scheduler engine can successfully place SOME subjects while
 * failing to place others (e.g. no remaining weekly slots, every
 * eligible faculty already at their cap, etc). Previously those
 * failures were only printed to the server log - the API always
 * reported "Timetable Generated Successfully" even when a subject
 * (like "Matrices") silently got zero periods.
 *
 * `entries` is what was actually saved. `failures` explains, in plain
 * language, which subject/hours could not be placed and why, so the
 * gap is visible in the UI instead of only in server logs.
 */
@Data
@Builder
public class TimetableGenerationResponse {

    private List<TimetableResponse> entries;

    private List<String> failures;
}
