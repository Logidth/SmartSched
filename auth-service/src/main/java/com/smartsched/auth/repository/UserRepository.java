package com.smartsched.auth.repository;

import com.smartsched.auth.entity.User;
import com.smartsched.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByFacultyId(Long facultyId);

    Optional<User> findByBranchId(Long branchId);

    List<User> findByRole(Role role);

}