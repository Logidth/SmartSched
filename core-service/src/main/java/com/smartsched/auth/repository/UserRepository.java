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

    /**
     * Used to resolve the department Admin(s) that should be notified
     * when their HOD approves/rejects a timetable. A branch normally
     * has exactly one ADMIN, but this returns a List (rather than
     * reusing findByBranchId's Optional<User>) since a branch's HOD
     * shares the same branch_id and would otherwise collide with a
     * single-result query.
     */
    List<User> findByBranchIdAndRole(Long branchId, Role role);

}