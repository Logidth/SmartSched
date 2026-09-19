package com.smartsched.faculty.service.impl;

import com.smartsched.auth.entity.User;
import com.smartsched.auth.repository.UserRepository;
import com.smartsched.branch.entity.Branch;
import com.smartsched.branch.repository.BranchRepository;
import com.smartsched.common.enums.Role;
import com.smartsched.common.enums.Status;
import com.smartsched.common.exception.BadRequestException;
import com.smartsched.common.exception.ConflictException;
import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.faculty.dto.FacultyCredentialsRequest;
import com.smartsched.faculty.dto.FacultyRequest;
import com.smartsched.faculty.dto.FacultyResponse;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.faculty.mapper.FacultyMapper;
import com.smartsched.faculty.repository.FacultyRepository;
import com.smartsched.faculty.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FacultyMapper mapper;

    @Override
    public FacultyResponse create(FacultyRequest request) {

        facultyRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .ifPresent(f ->
                { throw new ConflictException("Email already exists."); });

        // Login credentials are required when creating a faculty
        // member — without them there is no way for this person to
        // ever sign in.
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("Username is required.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required.");
        }

        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists.");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        Faculty faculty = mapper.toEntity(request);
        long count = facultyRepository.count() + 1;
        String employeeId = "EMP" + String.format("%03d", count);
        faculty.setEmployeeId(employeeId);

        faculty.setBranch(branch);

        Faculty saved = facultyRepository.save(faculty);

        // Create the login for this faculty member, mirroring how
        // PrincipalUserServiceImpl#createHod bundles the Faculty +
        // User creation together.
        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.FACULTY);
        user.setBranch(branch);
        user.setFaculty(saved);
        user.setActive(true);
        user.setAccountLocked(false);
        user.setFirstLogin(true);

        user = userRepository.save(user);

        // Keep the inverse side of the Faculty <-> User relationship
        // in sync too, since FacultyRepository#findByUser and the
        // mapper both resolve it via faculties.user_id.
        saved.setUser(user);
        saved = facultyRepository.save(saved);

        return mapper.toResponse(saved);
    }

    @Override
    public FacultyResponse setCredentials(
            Long id,
            FacultyCredentialsRequest request
    ) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));

        User user = faculty.getUser();

        if (user == null) {

            // No login exists yet for this faculty member (e.g. a
            // record created before username/password were required,
            // or seeded directly) - a username must be supplied to
            // create one now.
            if (request.getUsername() == null
                    || request.getUsername().isBlank()) {

                throw new BadRequestException(
                        "This faculty member has no login yet. " +
                                "A username is required to create one."
                );
            }

            String username = request.getUsername().trim();

            if (userRepository.existsByUsername(username)) {
                throw new ConflictException("Username already exists.");
            }

            user = new User();
            user.setUsername(username);
            user.setRole(Role.FACULTY);
            user.setBranch(faculty.getBranch());
            user.setFaculty(faculty);
            user.setActive(true);
            user.setAccountLocked(false);

        }
        // Existing login: username is left as-is. Resetting the
        // username here would silently break their old sign-in
        // without a clear "you changed my username" signal, so that
        // is intentionally out of scope for a password reset.

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        user.setFirstLogin(true);

        user = userRepository.save(user);

        faculty.setUser(user);
        faculty = facultyRepository.save(faculty);

        return mapper.toResponse(faculty);
    }

    @Override
    public FacultyResponse update(Long id, FacultyRequest request) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));



        facultyRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Email already exists.");
                    }
                });

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found."));

        mapper.updateEntity(faculty, request);

        faculty.setBranch(branch);

        Faculty updated = facultyRepository.save(faculty);

        return mapper.toResponse(updated);
    }

    @Override
    public FacultyResponse getById(Long id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));

        return mapper.toResponse(faculty);
    }

    @Override
    public List<FacultyResponse> getAll() {

        return facultyRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<FacultyResponse> getByBranch(Long branchId) {

        return facultyRepository.findByBranchId(branchId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public FacultyResponse activate(Long id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));

        faculty.setStatus(Status.ACTIVE);

        return mapper.toResponse(facultyRepository.save(faculty));
    }

    @Override
    public FacultyResponse deactivate(Long id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));

        faculty.setStatus(Status.INACTIVE);

        return mapper.toResponse(facultyRepository.save(faculty));
    }

    @Override
    public void delete(Long id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found."));

        faculty.setStatus(Status.INACTIVE);

        facultyRepository.save(faculty);
    }
}