package com.smartsched.facultyavailability.dto;

import com.smartsched.scheduler.enums.WorkingDay;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyAvailabilityRequest {

    private Long facultyId;

    private WorkingDay day;

    private Integer period;

    private Boolean available;
}