package com.smartsched.substitute.service;

import com.smartsched.substitute.dto.AssignSubstituteRequest;
import com.smartsched.substitute.dto.SubstituteResponse;

import java.util.List;

public interface SubstituteService {

    SubstituteResponse assignSubstitute(
            AssignSubstituteRequest request
    );

    SubstituteResponse getAssignment(
            Long id
    );

    List<SubstituteResponse> getFacultyAssignments(
            Long facultyId
    );

    List<SubstituteResponse> getAllAssignments();

}