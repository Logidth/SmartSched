package com.smartsched.progress.service.impl;

import com.smartsched.common.exception.ResourceNotFoundException;
import com.smartsched.facultyassignment.entity.FacultyAssignment;
import com.smartsched.facultyassignment.repository.FacultyAssignmentRepository;
import com.smartsched.lecture.enums.LectureStatus;
import com.smartsched.lecture.repository.LectureLogRepository;
import com.smartsched.progress.dto.SubjectProgressResponse;
import com.smartsched.progress.service.ProgressService;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.studentclass.repository.StudentClassRepository;
import com.smartsched.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final StudentClassRepository studentClassRepository;

    private final FacultyAssignmentRepository facultyAssignmentRepository;

    private final LectureLogRepository lectureLogRepository;

    @Override
    public List<SubjectProgressResponse> getClassProgress(
            Long studentClassId) {

        StudentClass studentClass =
                studentClassRepository.findById(studentClassId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Student Class Not Found"));

        List<FacultyAssignment> assignments =
                facultyAssignmentRepository.findByStudentClass(studentClass);

        List<SubjectProgressResponse> response =
                new ArrayList<>();

        for (FacultyAssignment assignment : assignments) {

            Subject subject =
                    assignment.getCurriculumSubject().getSubject();

            long completedHours =
                    lectureLogRepository
                            .countByTimetableEntry_StudentClass_IdAndTimetableEntry_Subject_IdAndStatus(
                                    studentClassId,
                                    subject.getId(),
                                    LectureStatus.COMPLETED
                            );

            int totalHours = subject.getTotalHours();

            int remainingHours =
                    Math.max(totalHours - (int) completedHours, 0);

            double percentage =
                    totalHours == 0
                            ? 0
                            : (completedHours * 100.0) / totalHours;

            response.add(

                    SubjectProgressResponse.builder()

                            .studentClassId(studentClass.getId())

                            .className(
                                    studentClass.getBranch().getName()
                                            + " "
                                            + studentClass.getYear()
                                            + "-"
                                            + studentClass.getSection())

                            .subjectId(subject.getId())

                            .subjectCode(subject.getSubjectCode())

                            .subjectName(subject.getSubjectName())

                            .totalHours(totalHours)

                            .completedHours((int) completedHours)

                            .remainingHours(remainingHours)

                            .completionPercentage(
                                    Math.round(percentage * 100.0) / 100.0)

                            .build()

            );
        }

        return response;
    }

    @Override
    public SubjectProgressResponse getSubjectProgress(
            Long studentClassId,
            Long subjectId) {

        return getClassProgress(studentClassId)
                .stream()
                .filter(p -> p.getSubjectId().equals(subjectId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject Progress Not Found"));
    }
}