package com.smartsched.faculty.dto;

import com.smartsched.common.enums.FacultyDesignation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacultyResponse {

    private Long id;

    private String employeeId;

    private String name;

    private String email;

    private String phone;

    private String username;

    private Long branchId;

    private String branchName;

    private FacultyDesignation designation;

    private String status;

    private Integer maxWeeklyHours;
}