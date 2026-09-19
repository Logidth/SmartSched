package com.smartsched.lecture.service;

import com.smartsched.lecture.dto.*;

import java.util.List;

public interface LectureLogService {

    LectureLogResponse startLecture(
            LectureStartRequest request
    );

    LectureLogResponse completeLecture(
            LectureCompleteRequest request
    );

    LectureLogResponse cancelLecture(
            LectureCancelRequest request
    );

    LectureLogResponse getLecture(
            Long id
    );

    List<LectureLogResponse> getTodaysLectures();

    List<LectureLogResponse> getAllLectures();
}