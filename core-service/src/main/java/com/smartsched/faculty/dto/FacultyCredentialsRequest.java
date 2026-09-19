package com.smartsched.faculty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Used for both:
 *   - Setting a login for a faculty member that doesn't have one yet
 *     (username is required in this case)
 *   - Resetting the password for a faculty member that already has
 *     a login (username is ignored - the existing username is kept)
 */
@Getter
@Setter
public class FacultyCredentialsRequest {

    private String username;

    @NotBlank
    private String newPassword;
}