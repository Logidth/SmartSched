package com.smartsched.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class LectureLogResponse {

    private Long id;

    private String branch;

    private Integer year;

    private Integer semester;

    private String section;

    private String subjectCode;

    private String subjectName;

    private String faculty;

    private String room;

    private String day;

    private Integer period;

    private LocalDate lectureDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String status;

    private String remarks;

    private String topicsCovered;

    private Integer attendanceCount;

    private Integer totalStudents;

    private Double attendancePercentage;

}