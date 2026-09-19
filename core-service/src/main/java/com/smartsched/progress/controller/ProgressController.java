package com.smartsched.progress.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.progress.dto.SubjectProgressResponse;
import com.smartsched.progress.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/class/{studentClassId}")
    public ApiResponse<List<SubjectProgressResponse>> getClassProgress(
            @PathVariable Long studentClassId) {

        return new ApiResponse<>(
                true,
                "Class Progress Retrieved Successfully",
                progressService.getClassProgress(studentClassId),
                LocalDateTime.now()
        );
    }

    @GetMapping("/class/{studentClassId}/subject/{subjectId}")
    public ApiResponse<SubjectProgressResponse> getSubjectProgress(
            @PathVariable Long studentClassId,
            @PathVariable Long subjectId) {

        return new ApiResponse<>(
                true,
                "Subject Progress Retrieved Successfully",
                progressService.getSubjectProgress(
                        studentClassId,
                        subjectId
                ),
                LocalDateTime.now()
        );
    }

}