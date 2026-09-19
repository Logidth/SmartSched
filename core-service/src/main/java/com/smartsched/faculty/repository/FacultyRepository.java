package com.smartsched.faculty.repository;

import com.smartsched.auth.entity.User;
import com.smartsched.faculty.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByEmployeeId(String employeeId);

    Optional<Faculty> findByEmail(String email);

    List<Faculty> findByBranchId(Long branchId);

    long countByBranchId(Long branchId);

    long count();

    Optional<Faculty> findByUser(User user);
}