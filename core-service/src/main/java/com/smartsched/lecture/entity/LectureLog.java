package com.smartsched.lecture.entity;

import com.smartsched.common.entity.BaseEntity;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.scheduler.entity.TimetableEntry;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(
        name = "lecture_logs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "timetable_entry_id",
                        "lecture_date"
                })
        }
)
public class LectureLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_entry_id")
    private TimetableEntry timetableEntry;

    @Column(name = "lecture_date", nullable = false)
    private LocalDate lectureDate;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureStatus status = LectureStatus.PENDING;

    @Column(length = 500)
    private String remarks;
    @Column(nullable = false)
    private Boolean progressUpdated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private Faculty completedBy;
    @Column(length = 2000)
    private String topicsCovered;

    @Column
    private Integer attendanceCount;

    @Column
    private Integer totalStudents;

    @Column
    private Double attendancePercentage;



    

}