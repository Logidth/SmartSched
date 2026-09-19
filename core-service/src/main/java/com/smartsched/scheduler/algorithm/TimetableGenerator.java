package com.smartsched.scheduler.algorithm;

import com.smartsched.scheduler.entity.TimetableEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableGenerator {

    private final ConstraintChecker constraintChecker;

    public List<TimetableEntry> generate() {

        List<TimetableEntry> timetable = new ArrayList<>();

        // Algorithm will be implemented here

        return timetable;

    }

}