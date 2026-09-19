package com.smartsched.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class LectureCompleteRequest {

    @NotNull
    private Long lectureLogId;

    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String topicsCovered;

    private String remarks;
}