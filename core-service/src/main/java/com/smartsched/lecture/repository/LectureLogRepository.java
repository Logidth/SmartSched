package com.smartsched.lecture.repository;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.lecture.entity.LectureLog;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LectureLogRepository
        extends JpaRepository<LectureLog, Long> {

    Optional<LectureLog> findByTimetableEntryAndLectureDate(
            TimetableEntry timetableEntry,
            LocalDate lectureDate
    );

    boolean existsByTimetableEntryAndLectureDate(
            TimetableEntry timetableEntry,
            LocalDate lectureDate
    );

    List<LectureLog> findByStatus(
            LectureStatus status
    );

    List<LectureLog> findByLectureDate(
            LocalDate lectureDate
    );

    List<LectureLog> findByTimetableEntry(
            TimetableEntry timetableEntry
    );
    List<LectureLog> findByTimetableEntry_FacultyAndLectureDate(
            Faculty faculty,
            LocalDate lectureDate
    );

    long countByStatus(LectureStatus status);

    long countByTimetableEntryFacultyIdAndStatus(
            Long facultyId,
            LectureStatus status
    );

    @Query("""
SELECT COUNT(l)
FROM LectureLog l
WHERE l.status = 'COMPLETED'
AND l.timetableEntry.studentClass.branch.id = :branchId
""")
    long countCompletedByBranch(Long branchId);




    long countByTimetableEntry_StudentClass_IdAndTimetableEntry_Subject_IdAndStatus(
            Long studentClassId,
            Long subjectId,
            LectureStatus status
    );

    List<LectureLog> findByTimetableEntry_StudentClass_Id(
            Long studentClassId
    );
    @Query("""
SELECT COUNT(l)
FROM LectureLog l
WHERE l.timetableEntry.studentClass = :studentClass
AND l.timetableEntry.subject = :subject
AND l.status = 'COMPLETED'
""")
    long countCompletedLectures(
            @Param("studentClass") StudentClass studentClass,
            @Param("subject") Subject subject
    );

    @Query("""
SELECT COUNT(l)
FROM LectureLog l
WHERE l.timetableEntry.studentClass.branch.id = :branchId
""")
    long countTotalByBranch(@Param("branchId") Long branchId);


    Optional<LectureLog> findById(Long id);

    List<LectureLog> findByTimetableEntryFacultyId(Long facultyId);

    List<LectureLog> findByLectureDateAndStatus(
            LocalDate lectureDate,
            LectureStatus status
    );

}