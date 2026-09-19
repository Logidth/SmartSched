package com.smartsched.scheduler.engine;

import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.studentclass.entity.StudentClass;

public interface SlotFinder {

    Slot findSlot(
            StudentClass studentClass,
            Faculty faculty,
            Room room,
            SchedulerContext context
    );

}