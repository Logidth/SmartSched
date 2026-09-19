package com.smartsched.scheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveTimetableRequest {

    private Long studentClassId;

    private Long academicYearId;

    private Boolean approved;

    private String remarks;

}