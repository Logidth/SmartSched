package com.smartsched.academicyear.repository;

import com.smartsched.academicyear.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYear,Long> {

    Optional<AcademicYear> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}