package com.smartsched.scheduler.algorithm;

import com.smartsched.scheduler.entity.TimetableEntry;
import com.smartsched.scheduler.enums.WorkingDay;

import java.util.HashMap;
import java.util.Map;

public class TimetableMatrix {

    private final Map<WorkingDay,
            Map<Integer, TimetableEntry>> matrix =
            new HashMap<>();

    public TimetableMatrix() {

        for (WorkingDay day : WorkingDay.values()) {

            matrix.put(day, new HashMap<>());

        }

    }

    public boolean isEmpty(
            WorkingDay day,
            int period) {

        return !matrix.get(day).containsKey(period);

    }

    public void put(
            WorkingDay day,
            int period,
            TimetableEntry entry) {

        matrix.get(day).put(period, entry);

    }

    public TimetableEntry get(
            WorkingDay day,
            int period) {

        return matrix.get(day).get(period);

    }

}