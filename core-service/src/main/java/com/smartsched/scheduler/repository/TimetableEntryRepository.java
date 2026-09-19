package com.smartsched.scheduler.repository;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.TimetableStatus;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimetableEntryRepository
        extends JpaRepository<TimetableEntry, Long> {

    List<TimetableEntry> findByStudentClass(StudentClass studentClass);

    List<TimetableEntry> findByFaculty(Faculty faculty);

    List<TimetableEntry> findByRoom(Room room);

    boolean existsByStudentClassAndDayAndPeriodNumber(
            StudentClass studentClass,
            WorkingDay day,
            Integer periodNumber
    );

    boolean existsByFacultyAndDayAndPeriodNumber(
            Faculty faculty,
            WorkingDay day,
            Integer periodNumber
    );

    boolean existsByRoomAndDayAndPeriodNumber(
            Room room,
            WorkingDay day,
            Integer periodNumber
    );

    List<TimetableEntry> findByAcademicYear(AcademicYear academicYear);

    List<TimetableEntry> findByStudentClassAndAcademicYear(
            StudentClass studentClass,
            AcademicYear academicYear
    );

    List<TimetableEntry> findByFacultyOrderByDayAscPeriodNumberAsc(
            Faculty faculty
    );
    long countByApprovedTrue();

    long countByApprovedFalse();

    @Query("""
SELECT COUNT(t)
FROM TimetableEntry t
WHERE t.studentClass.branch.id = :branchId
AND t.approved = true
""")
    long countApprovedByBranch(Long branchId);

    @Query("""
SELECT COUNT(t)
FROM TimetableEntry t
WHERE t.studentClass.branch.id = :branchId
AND t.approved = false
""")
    long countPendingByBranch(Long branchId);

    /*
     * A "timetable" is one (studentClass, academicYear) pair, made up of
     * many period-level TimetableEntry rows (one per day/period). The
     * plain count* queries above count rows, so a single pending
     * timetable with e.g. 55 periods was reported as "55 pending
     * timetables" on the dashboards. These distinct-pair variants count
     * timetables instead of periods.
     */
    @Query("""
SELECT COUNT(DISTINCT CONCAT(t.studentClass.id, '-', t.academicYear.id))
FROM TimetableEntry t
WHERE t.approved = true
""")
    long countDistinctApprovedTimetables();

    @Query("""
SELECT COUNT(DISTINCT CONCAT(t.studentClass.id, '-', t.academicYear.id))
FROM TimetableEntry t
WHERE t.approved = false
""")
    long countDistinctPendingTimetables();

    @Query("""
SELECT COUNT(DISTINCT CONCAT(t.studentClass.id, '-', t.academicYear.id))
FROM TimetableEntry t
WHERE t.studentClass.branch.id = :branchId
AND t.approved = true
""")
    long countDistinctApprovedTimetablesByBranch(Long branchId);

    @Query("""
SELECT COUNT(DISTINCT CONCAT(t.studentClass.id, '-', t.academicYear.id))
FROM TimetableEntry t
WHERE t.studentClass.branch.id = :branchId
AND t.approved = false
""")
    long countDistinctPendingTimetablesByBranch(Long branchId);

    @Query("""
SELECT COUNT(DISTINCT CONCAT(t.studentClass.id, '-', t.academicYear.id))
FROM TimetableEntry t
WHERE t.status = :status
""")
    long countDistinctByStatus(TimetableStatus status);

    boolean existsByStudentClassAndSubjectAndDay(
            StudentClass studentClass,
            Subject subject,
            WorkingDay day
    );

    long countByFacultyId(Long facultyId);

    boolean existsByStudentClassIdAndAcademicYearIdAndDayAndPeriodNumber(
            Long studentClassId,
            Long academicYearId,
            WorkingDay day,
            Integer periodNumber
    );

    boolean existsByFacultyIdAndAcademicYearIdAndDayAndPeriodNumber(
            Long facultyId,
            Long academicYearId,
            WorkingDay day,
            Integer periodNumber
    );

    boolean existsByRoomIdAndAcademicYearIdAndDayAndPeriodNumber(
            Long roomId,
            Long academicYearId,
            WorkingDay day,
            Integer periodNumber
    );
    List<TimetableEntry> findByStudentClassIdAndStatus(
            Long classId,
            TimetableStatus status
    );

    List<TimetableEntry> findByStudentClassId(
            Long classId
    );

    boolean existsByStudentClassIdAndStatus(
            Long classId,
            TimetableStatus status
    );

    List<TimetableEntry> findByStudentClassIdOrderByDayAscPeriodNumberAsc(
            Long classId
    );

    List<TimetableEntry> findByFacultyIdOrderByDayAscPeriodNumberAsc(
            Long facultyId
    );

    List<TimetableEntry> findByRoomIdOrderByDayAscPeriodNumberAsc(
            Long roomId
    );

    List<TimetableEntry> findByDayOrderByPeriodNumberAsc(
            WorkingDay day
    );

    List<TimetableEntry> findByStudentClassBranchIdOrderByDayAscPeriodNumberAsc(
            Long branchId
    );

    List<TimetableEntry> findByStudentClassBranchId(Long branchId);

    @Query("""
SELECT t
FROM TimetableEntry t
JOIN FETCH t.studentClass sc
JOIN FETCH sc.branch
JOIN FETCH t.subject
JOIN FETCH t.faculty
JOIN FETCH t.room
JOIN FETCH t.academicYear
WHERE sc.id = :classId
ORDER BY t.day, t.periodNumber
""")
    List<TimetableEntry> findClassTimetable(Long classId);

    List<TimetableEntry> findByStatusOrderByStudentClassAsc(
            TimetableStatus status
    );

    long countByStatus(TimetableStatus status);



    /**
     * Same data as findByFacultyOrderByDayAscPeriodNumberAsc, but
     * fetch-joins every association the mapper needs (subject, room,
     * studentClass + its branch, academicYear) in one query. Without
     * this, those associations come back as lazy proxies that blow
     * up with LazyInitializationException the moment the mapper
     * touches them outside the (closed) Hibernate session - and even
     * where it doesn't blow up, the plain finder would otherwise
     * trigger a separate query per association per row (N+1).
     */
    @Query("""
SELECT t
FROM TimetableEntry t
JOIN FETCH t.studentClass sc
JOIN FETCH sc.branch
JOIN FETCH t.subject
JOIN FETCH t.faculty
JOIN FETCH t.room
JOIN FETCH t.academicYear
WHERE t.faculty.id = :facultyId
ORDER BY t.day, t.periodNumber
""")
    List<TimetableEntry> findFacultyTimetable(Long facultyId);

}