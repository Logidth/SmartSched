package com.smartsched.auth.service;

import com.smartsched.auth.dto.ChangePasswordRequest;
import com.smartsched.auth.dto.LoginRequest;
import com.smartsched.auth.dto.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    void changePassword(ChangePasswordRequest request);

}