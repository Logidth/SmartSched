package com.smartsched.scheduler.engine;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotFinderImpl implements SlotFinder {
    private final SchedulerValidator validator;
    @Override
    public Slot findSlot(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            SchedulerContext context
    ) {

        return null;
    }
}