package com.smartsched.workload.repository;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.workload.entity.FacultyWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyWorkloadRepository
        extends JpaRepository<FacultyWorkload, Long> {

    Optional<FacultyWorkload> findByFaculty(Faculty faculty);

}