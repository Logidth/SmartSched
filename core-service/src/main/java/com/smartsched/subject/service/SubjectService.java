package com.smartsched.subject.service;

import com.smartsched.subject.dto.SubjectRequest;
import com.smartsched.subject.dto.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse create(SubjectRequest request);

    SubjectResponse update(Long id, SubjectRequest request);

    SubjectResponse getById(Long id);

    List<SubjectResponse> getAll();

    List<SubjectResponse> getByRegulation(Long regulationId);

    SubjectResponse activate(Long id);

    SubjectResponse deactivate(Long id);

    void delete(Long id);

}