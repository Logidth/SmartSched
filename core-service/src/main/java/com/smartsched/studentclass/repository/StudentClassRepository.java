package com.smartsched.studentclass.repository;

import com.smartsched.studentclass.entity.StudentClass;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface StudentClassRepository
        extends JpaRepository<StudentClass, Long> {

    List<StudentClass> findByBranchId(Long branchId);

    /**
     * Same lookup as findById, but takes a DB-level row lock
     * (SELECT ... FOR UPDATE) that is held until the current
     * transaction commits or rolls back.
     *
     * Used by SchedulerServiceImpl#generateTimetable to serialize
     * concurrent "generate timetable" requests for the same class,
     * so two overlapping requests can never both try to INSERT into
     * the same (student_class_id, academic_year_id, day, period)
     * slot at once - see the comment there for details.
     *
     * Explicit lock timeout: without this, a blocked waiter sits on
     * MySQL's innodb_lock_wait_timeout default (50s) before failing.
     * Since the holder of this lock runs the full scheduling
     * algorithm plus the delete/insert of timetable entries before
     * releasing it, that default is a long time for a request thread
     * to sit blocked. Failing fast (5s) lets the caller retry
     * quickly instead of a thread being tied up for 50s only to get
     * a raw lock-wait-timeout exception anyway.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Query("SELECT sc FROM StudentClass sc WHERE sc.id = :id")
    Optional<StudentClass> findByIdForUpdate(Long id);

    Optional<StudentClass> findByAcademicYearIdAndRegulationIdAndBranchIdAndYearAndSemesterAndSection(
            Long academicYearId,
            Long regulationId,
            Long branchId,
            Integer year,
            Integer semester,
            String section
    );

    List<StudentClass> findByStatus(com.smartsched.common.enums.Status status);
    long countByBranchId(Long branchId);
    long count();
}