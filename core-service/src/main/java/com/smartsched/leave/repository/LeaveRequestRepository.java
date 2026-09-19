package com.smartsched.leave.repository;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.leave.entity.LeaveRequest;
import com.smartsched.leave.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN FETCH l.faculty f " +
            "LEFT JOIN FETCH f.branch " +
            "WHERE l.faculty = :faculty")
    List<LeaveRequest> findByFaculty(@Param("faculty") Faculty faculty);

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN FETCH l.faculty f " +
            "LEFT JOIN FETCH f.branch " +
            "WHERE l.status = :status")
    List<LeaveRequest> findByStatus(@Param("status") LeaveStatus status);

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN FETCH l.faculty f " +
            "LEFT JOIN FETCH f.branch b " +
            "WHERE l.status = :status AND b.id = :branchId")
    List<LeaveRequest> findByStatusAndFacultyBranchId(
            @Param("status") LeaveStatus status,
            @Param("branchId") Long branchId
    );

    List<LeaveRequest> findByFacultyAndStatus(
            Faculty faculty,
            LeaveStatus status
    );

    boolean existsByFacultyAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Faculty faculty,
            LeaveStatus status,
            LocalDate date1,
            LocalDate date2
    );

    boolean existsByFacultyAndStatusInAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Faculty faculty,
            List<LeaveStatus> statuses,
            LocalDate toDate,
            LocalDate fromDate
    );

    long countByStatus(LeaveStatus status);

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN FETCH l.faculty f " +
            "LEFT JOIN FETCH f.branch")
    List<LeaveRequest> findAllWithFacultyAndBranch();

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN FETCH l.faculty f " +
            "LEFT JOIN FETCH f.branch " +
            "WHERE l.id = :id")
    Optional<LeaveRequest> findByIdWithFacultyAndBranch(@Param("id") Long id);
}