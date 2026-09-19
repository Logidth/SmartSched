package com.smartsched.auth.service.impl;

import com.smartsched.auth.dto.ChangePasswordRequest;
import com.smartsched.auth.dto.LoginRequest;
import com.smartsched.auth.dto.LoginResponse;
import com.smartsched.auth.entity.User;
import com.smartsched.auth.repository.UserRepository;
import com.smartsched.auth.service.AuthenticationService;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.security.jwt.JwtService;
import com.smartsched.security.service.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl
        implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));



        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        user = userRepository.findByUsername(
                        request.getUsername())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid Username or Password"));

        String token = jwtService.generateToken(
                new CustomUserDetails(user)
        );

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .firstLogin(user.isFirstLogin())
                .branchId(user.getBranch() != null ? user.getBranch().getId() : null)
                .branchName(user.getBranch() != null ? user.getBranch().getName() : null)
                .facultyId(user.getFaculty() != null ? user.getFaculty().getId() : null)
                .facultyName(user.getFaculty() != null ? user.getFaculty().getName() : null)
                .build();
    }


    @Override
    public void changePassword(ChangePasswordRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new BadCredentialsException("Old password is incorrect");
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from old password");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        user.setFirstLogin(false);

        userRepository.save(user);
    }
}