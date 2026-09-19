package com.smartsched.lecture.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureCancelRequest {

    @NotNull
    private Long lectureLogId;

    private String remarks;
}