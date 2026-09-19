package com.smartsched.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String tokenType;

    private Long userId;

    private String username;

    private String role;

    private boolean firstLogin;

    // Department context — needed by ADMIN (and HOD) so the
    // frontend knows which branch/department to scope
    // faculty, subjects, classes and timetable generation to.
    private Long branchId;

    private String branchName;

    // Faculty context — needed by FACULTY so the frontend can show
    // a friendly name. NOTE: the frontend should NOT use facultyId
    // to build API calls — every faculty self-service endpoint
    // (/api/faculty/me/**) resolves the faculty from the JWT on the
    // server side, so this is for display purposes only.
    private Long facultyId;

    private String facultyName;
}