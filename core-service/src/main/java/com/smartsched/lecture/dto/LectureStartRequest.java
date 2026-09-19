package com.smartsched.lecture.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class LectureStartRequest {

    @NotNull
    private Long timetableEntryId;

    @NotNull
    private LocalDate lectureDate;

    @NotNull
    private LocalTime startTime;
}