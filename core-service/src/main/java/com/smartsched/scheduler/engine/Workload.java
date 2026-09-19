
        package com.smartsched.scheduler.engine;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.subject.entity.Subject;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Workload {

    private Faculty faculty;

    private Subject subject;

    private int remainingHours;

    public boolean completed() {
        return remainingHours <= 0;
    }

    public void allocateOneHour() {
        if (remainingHours > 0) {
            remainingHours--;
        }
    }
}
