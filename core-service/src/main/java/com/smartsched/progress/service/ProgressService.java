package com.smartsched.progress.service;

import com.smartsched.progress.dto.SubjectProgressResponse;

import java.util.List;

public interface ProgressService {

    List<SubjectProgressResponse> getClassProgress(
            Long studentClassId
    );

    SubjectProgressResponse getSubjectProgress(
            Long studentClassId,
            Long subjectId
    );

}