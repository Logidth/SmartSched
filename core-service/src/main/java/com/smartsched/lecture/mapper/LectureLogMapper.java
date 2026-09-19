package com.smartsched.lecture.mapper;

import com.smartsched.lecture.dto.LectureLogResponse;
import com.smartsched.lecture.entity.LectureLog;
import org.springframework.stereotype.Component;

@Component
public class LectureLogMapper {

    public LectureLogResponse map(LectureLog lecture) {

        return LectureLogResponse.builder()

                .id(lecture.getId())

                .branch(
                        lecture.getTimetableEntry()
                                .getStudentClass()
                                .getBranch()
                                .getName())

                .year(
                        lecture.getTimetableEntry()
                                .getStudentClass()
                                .getYear())

                .semester(
                        lecture.getTimetableEntry()
                                .getStudentClass()
                                .getSemester())

                .section(
                        lecture.getTimetableEntry()
                                .getStudentClass()
                                .getSection())

                .subjectCode(
                        lecture.getTimetableEntry()
                                .getSubject()
                                .getSubjectCode())

                .subjectName(
                        lecture.getTimetableEntry()
                                .getSubject()
                                .getSubjectName())

                .faculty(
                        lecture.getTimetableEntry()
                                .getFaculty()
                                .getName())

                .room(
                        lecture.getTimetableEntry()
                                .getRoom()
                                .getRoomNumber())

                .day(
                        lecture.getTimetableEntry()
                                .getDay()
                                .name())

                .period(
                        lecture.getTimetableEntry()
                                .getPeriodNumber())

                .lectureDate(
                        lecture.getLectureDate())

                .startTime(
                        lecture.getStartTime())

                .endTime(
                        lecture.getEndTime())

                .status(
                        lecture.getStatus().name())

                .remarks(
                        lecture.getRemarks())

                .build();

    }

}