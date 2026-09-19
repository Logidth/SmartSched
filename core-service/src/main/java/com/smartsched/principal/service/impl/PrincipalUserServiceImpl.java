package com.smartsched.principal.service.impl;

import com.smartsched.auth.entity.User;
import com.smartsched.auth.repository.UserRepository;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.enums.FacultyDesignation;
import com.smartsched.common.enums.Role;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.principal.dto.CreateAdminRequest;
import com.smartsched.principal.dto.CreateHodRequest;
import com.smartsched.principal.dto.ResetPasswordRequest;
import com.smartsched.principal.dto.UserResponse;
import com.smartsched.principal.service.PrincipalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrincipalUserServiceImpl implements PrincipalUserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse createDepartmentAdmin(CreateAdminRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceNotFoundException("Username already exists.");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.ADMIN);

        user.setBranch(branch);

        user.setActive(true);
        user.setAccountLocked(false);
        user.setFirstLogin(true);

        user = userRepository.save(user);

        return map(user);
    }



    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAdmins() {

        return userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getHods() {

        return userRepository.findByRole(Role.HOD)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional
    public void enableUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(
            Long id,
            ResetPasswordRequest request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        user.setFirstLogin(true);

        userRepository.save(user);
    }

    private UserResponse map(User user) {

        return UserResponse.builder()

                .id(user.getId())

                .username(user.getUsername())

                .role(user.getRole().name())

                .active(user.isActive())

                .firstLogin(user.isFirstLogin())

                .branch(
                        user.getBranch() == null
                                ? null
                                : user.getBranch().getName()
                )

                .faculty(
                        user.getFaculty() == null
                                ? null
                                : user.getFaculty().getName()
                )

                .build();
    }

    @Override
    @Transactional
    public UserResponse createHod(CreateHodRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceNotFoundException("Username already exists.");
        }

        if (facultyRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
            throw new ResourceNotFoundException("Employee ID already exists.");
        }

        if (facultyRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists.");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found"));

        Faculty faculty = new Faculty();

        faculty.setName(request.getName());
        faculty.setEmployeeId(request.getEmployeeId());
        faculty.setEmail(request.getEmail());
        faculty.setPhone(request.getPhone());

        faculty.setBranch(branch);

        faculty.setDesignation(FacultyDesignation.PROFESSOR);

        faculty.setStatus(Status.ACTIVE);

        faculty.setMaxWeeklyHours(18);

        faculty = facultyRepository.save(faculty);

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.HOD);

        user.setFaculty(faculty);
        user.setBranch(branch);

        user.setActive(true);
        user.setFirstLogin(true);

        user = userRepository.save(user);

        // Keep the inverse side of the Faculty <-> User relationship in
        // sync as well, since some queries (e.g. FacultyRepository#findByUser)
        // resolve the HOD's Faculty record via faculties.user_id.
        faculty.setUser(user);
        facultyRepository.save(faculty);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .active(user.isActive())
                .firstLogin(user.isFirstLogin())
                .branch(branch.getName())
                .faculty(faculty.getName())
                .build();
    }
}