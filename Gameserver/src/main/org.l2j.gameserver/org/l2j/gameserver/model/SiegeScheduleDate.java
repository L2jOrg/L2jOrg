package org.l2j.gameserver.model;

import java.time.DayOfWeek;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class SiegeScheduleDate {
    private final DayOfWeek day;
    private final int hour;
    private final int maxConcurrent;

    public SiegeScheduleDate(DayOfWeek day, int hour, int maxConcurrent) {
        this.day = day;
        this.hour = hour;
        this.maxConcurrent = maxConcurrent;
    }

    public SiegeScheduleDate() {
        this.day = DayOfWeek.SUNDAY;
        this.hour = 20;
        this.maxConcurrent = 5;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }
}
