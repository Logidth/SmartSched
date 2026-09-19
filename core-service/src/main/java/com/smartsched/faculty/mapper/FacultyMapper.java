package com.smartsched.faculty.mapper;

import com.smartsched.common.enums.Status;
import com.smartsched.faculty.dto.FacultyRequest;
import com.smartsched.faculty.dto.FacultyResponse;
import com.smartsched.faculty.entity.Faculty;
import org.springframework.stereotype.Component;

@Component
public class FacultyMapper {

    public Faculty toEntity(FacultyRequest request) {

        Faculty faculty = new Faculty();


        faculty.setName(request.getName().trim());
        faculty.setEmail(request.getEmail().trim().toLowerCase());
        faculty.setPhone(request.getPhone().trim());
        faculty.setDesignation(request.getDesignation());
        faculty.setStatus(Status.ACTIVE);
        faculty.setMaxWeeklyHours(
                request.getMaxWeeklyHours() == null
                        ? 18
                        : request.getMaxWeeklyHours()
        );
        return faculty;
    }

    public FacultyResponse toResponse(Faculty faculty) {

        return FacultyResponse.builder()
                .id(faculty.getId())
                .employeeId(faculty.getEmployeeId())
                .name(faculty.getName())
                .email(faculty.getEmail())
                .phone(faculty.getPhone())
                .username(
                        faculty.getUser() != null
                                ? faculty.getUser().getUsername()
                                : null
                )
                .branchId(faculty.getBranch().getId())
                .branchName(faculty.getBranch().getName())
                .designation(faculty.getDesignation())
                .status(faculty.getStatus().name())
                .maxWeeklyHours(faculty.getMaxWeeklyHours())
                .build();
    }

    public void updateEntity(Faculty faculty,
                             FacultyRequest request) {


        faculty.setName(request.getName().trim());
        faculty.setEmail(request.getEmail().trim().toLowerCase());
        faculty.setPhone(request.getPhone().trim());
        faculty.setDesignation(request.getDesignation());
        faculty.setMaxWeeklyHours(request.getMaxWeeklyHours());

        // Branch is set in ServiceImpl after loading it
        // from BranchRepository.
    }
}