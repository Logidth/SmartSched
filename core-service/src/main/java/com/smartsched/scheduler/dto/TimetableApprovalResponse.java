package com.smartsched.scheduler.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableApprovalResponse {

    private Long studentClassId;

    private Long academicYearId;

    private Boolean approved;

    private String remarks;

    private Integer approvedEntries;

    private LocalDateTime approvedAt;
}