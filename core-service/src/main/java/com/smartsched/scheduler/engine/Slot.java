package com.smartsched.scheduler.engine;

import com.smartsched.scheduler.enums.WorkingDay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Slot {

    private WorkingDay day;

    private Integer period;

}