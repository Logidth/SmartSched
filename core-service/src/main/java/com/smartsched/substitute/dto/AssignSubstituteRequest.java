package com.smartsched.substitute.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignSubstituteRequest {

    private Long lectureLogId;

    private Long substituteFacultyId;

    private String reason;
}