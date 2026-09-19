package com.smartsched.principal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class CreateHodRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String employeeId;

    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private Long branchId;
}