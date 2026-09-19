package com.smartsched.substitute.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubstituteResponse {

    private Long id;

    private String originalFaculty;

    private String substituteFaculty;

    private String subject;

    private String className;

    private LocalDate assignmentDate;

    private LocalDateTime assignedAt;

    private Boolean accepted;

    private String reason;
}