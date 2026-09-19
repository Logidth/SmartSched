package com.smartsched.faculty.dto;

import com.smartsched.common.enums.FacultyDesignation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyRequest {

    // Login credentials — required when creating a new faculty
    // member (validated manually in FacultyServiceImpl.create()),
    // ignored on update since the login already exists by then.
    private String username;

    private String password;




    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private Long branchId;

    @NotNull
    private FacultyDesignation designation;

    private Integer maxWeeklyHours;

}