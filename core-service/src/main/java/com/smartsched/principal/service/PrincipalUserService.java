package com.smartsched.principal.service;

import com.smartsched.principal.dto.CreateAdminRequest;
import com.smartsched.principal.dto.CreateHodRequest;
import com.smartsched.principal.dto.ResetPasswordRequest;
import com.smartsched.principal.dto.UserResponse;

import java.util.List;

public interface PrincipalUserService {

    UserResponse createDepartmentAdmin(
            CreateAdminRequest request);

    UserResponse createHod(
            CreateHodRequest request);

    List<UserResponse> getAdmins();

    List<UserResponse> getHods();

    void enableUser(Long id);

    void disableUser(Long id);

    void resetPassword(
            Long id,
            ResetPasswordRequest request);


}