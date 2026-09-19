package com.smartsched.studentclass.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.studentclass.dto.StudentClassRequest;
import com.smartsched.studentclass.dto.StudentClassResponse;
import com.smartsched.studentclass.entity.StudentClass;
import org.springframework.stereotype.Component;

@Component
public class StudentClassMapper {

    public StudentClass toEntity(StudentClassRequest request) {

        StudentClass studentClass = new StudentClass();

        studentClass.setYear(request.getYear());
        studentClass.setSemester(request.getSemester());
        studentClass.setSection(request.getSection().trim().toUpperCase());
        studentClass.setStrength(request.getStrength());
        studentClass.setStatus(Status.ACTIVE);

        return studentClass;
    }

    public StudentClassResponse toResponse(StudentClass studentClass) {

        return StudentClassResponse.builder()
                .id(studentClass.getId())
                .branchId(studentClass.getBranch().getId())
                .branchName(studentClass.getBranch().getName())

                .academicYearId(studentClass.getAcademicYear().getId())
                .academicYear(studentClass.getAcademicYear().getName())

                .regulationId(studentClass.getRegulation().getId())
                .regulation(studentClass.getRegulation().getCode())

                .year(studentClass.getYear())
                .semester(studentClass.getSemester())
                .section(studentClass.getSection())
                .strength(studentClass.getStrength())
                .status(studentClass.getStatus().name())
                .build();
    }

    public void updateEntity(StudentClass studentClass,
                             StudentClassRequest request) {

        studentClass.setYear(request.getYear());
        studentClass.setSemester(request.getSemester());
        studentClass.setSection(request.getSection().trim().toUpperCase());
        studentClass.setStrength(request.getStrength());

        // Branch, AcademicYear and Regulation
        // are set in the ServiceImpl after loading
        // them from their repositories.
    }
}