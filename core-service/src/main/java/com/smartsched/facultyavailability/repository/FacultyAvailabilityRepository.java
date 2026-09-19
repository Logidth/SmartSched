package com.smartsched.facultyavailability.repository;

import com.smartsched.facultyavailability.entity.FacultyAvailability;
import com.smartsched.scheduler.enums.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyAvailabilityRepository
        extends JpaRepository<FacultyAvailability, Long> {

    boolean existsByFacultyIdAndDayAndPeriodAndAvailableFalse(
            Long facultyId,
            WorkingDay day,
            Integer period
    );
}
