package com.smartsched.branch.repository;

import com.smartsched.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByIdAndActiveTrue(Long id);

    Optional<Branch> findByCodeIgnoreCase(String code);

    Optional<Branch> findByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);
    long count();

    Optional<Branch> findById(Long id);
}