package com.smartsched.principal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String role;

    private boolean active;

    private boolean firstLogin;

    private String branch;

    private String faculty;
}