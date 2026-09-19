package com.smartsched.lecture.controller;

import com.smartsched.common.response.ApiResponse;
import com.smartsched.lecture.dto.LectureLogResponse;
import com.smartsched.lecture.service.LectureLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/faculty/lecture")
@RequiredArgsConstructor
public class LectureLogController {

    private final LectureLogService service;



    @GetMapping("/{id}")
    public ApiResponse<LectureLogResponse> getLecture(
            @PathVariable Long id){

        return new ApiResponse<>(

                true,

                "Lecture Retrieved Successfully",

                service.getLecture(id),

                LocalDateTime.now()

        );

    }

    @GetMapping("/today")
    public ApiResponse<List<LectureLogResponse>> today(){

        return new ApiResponse<>(

                true,

                "Today's Lectures Retrieved Successfully",

                service.getTodaysLectures(),

                LocalDateTime.now()

        );

    }

    @GetMapping
    public ApiResponse<List<LectureLogResponse>> all(){

        return new ApiResponse<>(

                true,

                "Lectures Retrieved Successfully",

                service.getAllLectures(),

                LocalDateTime.now()

        );

    }

}