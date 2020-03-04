package org.l2j.gameserver.model;

import java.util.Calendar;

/**
 * @author UnAfraid
 */
public class SiegeScheduleDate {
    private final int _day;
    private final int _hour;
    private final int _maxConcurrent;

    public SiegeScheduleDate(StatsSet set) {
        _day = set.getInt("day", Calendar.SUNDAY);
        _hour = set.getInt("hour", 16);
        _maxConcurrent = set.getInt("maxConcurrent", 5);
    }

    public int getDay() {
        return _day;
    }

    public int getHour() {
        return _hour;
    }

    public int getMaxConcurrent() {
        return _maxConcurrent;
    }
}
