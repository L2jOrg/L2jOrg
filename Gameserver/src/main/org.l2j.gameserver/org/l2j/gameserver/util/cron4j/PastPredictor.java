/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.util.cron4j;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author UnAfraid
 */
public class PastPredictor {
    /**
     * The scheduling pattern on which the predictor works.
     */
    private final SchedulingPattern _schedulingPattern;

    /**
     * The start time for the next prediction.
     */
    private long _time;

    /**
     * The time zone for the prediction.
     */
    private TimeZone _timeZone = TimeZone.getDefault();

    /**
     * It builds a predictor with the given scheduling pattern and start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @param start             The start time of the prediction.
     * @throws InvalidPatternException In the given scheduling pattern isn't valid.
     */
    public PastPredictor(String schedulingPattern, long start) throws InvalidPatternException {
        _schedulingPattern = new SchedulingPattern(schedulingPattern);
        _time = (start / (1000 * 60)) * 1000 * 60;
    }

    /**
     * It builds a predictor with the given scheduling pattern and start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @param start             The start time of the prediction.
     * @throws InvalidPatternException In the given scheduling pattern isn't valid.
     */
    public PastPredictor(String schedulingPattern, Date start) throws InvalidPatternException {
        this(schedulingPattern, start.getTime());
    }

    /**
     * It builds a predictor with the given scheduling pattern and the current system time as the prediction start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @throws InvalidPatternException In the given scheduling pattern isn't valid.
     */
    public PastPredictor(String schedulingPattern) throws InvalidPatternException {
        this(schedulingPattern, System.currentTimeMillis());
    }

    /**
     * It builds a predictor with the given scheduling pattern and start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @param start             The start time of the prediction.
     * @since 2.0
     */
    public PastPredictor(SchedulingPattern schedulingPattern, long start) {
        _schedulingPattern = schedulingPattern;
        _time = (start / (1000 * 60)) * 1000 * 60;
    }

    /**
     * It builds a predictor with the given scheduling pattern and start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @param start             The start time of the prediction.
     * @since 2.0
     */
    public PastPredictor(SchedulingPattern schedulingPattern, Date start) {
        this(schedulingPattern, start.getTime());
    }

    /**
     * It builds a predictor with the given scheduling pattern and the current system time as the prediction start time.
     *
     * @param schedulingPattern The pattern on which the prediction will be based.
     * @since 2.0
     */
    public PastPredictor(SchedulingPattern schedulingPattern) {
        this(schedulingPattern, System.currentTimeMillis());
    }

    /**
     * Sets the time zone for predictions.
     *
     * @param timeZone The time zone for predictions.
     * @since 2.2.5
     */
    public void setTimeZone(TimeZone timeZone) {
        _timeZone = timeZone;
    }

    /**
     * It returns the previous matching moment as a millis value.
     *
     * @return The previous matching moment as a millis value.
     */
    public synchronized long prevMatchingTime() {
        // Go a minute back.
        _time -= 60000;
        // Is it matching?
        if (_schedulingPattern.match(_time)) {
            return _time;
        }
        // Go through the matcher groups.
        int size = _schedulingPattern.matcherSize;
        long[] times = new long[size];
        for (int k = 0; k < size; k++) {
            // Ok, split the time!
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(_time);
            c.setTimeZone(_timeZone);
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            // Gets the matchers.
            ValueMatcher minuteMatcher = _schedulingPattern.minuteMatchers.get(k);
            ValueMatcher hourMatcher = _schedulingPattern.hourMatchers.get(k);
            ValueMatcher dayOfMonthMatcher = _schedulingPattern.dayOfMonthMatchers.get(k);
            ValueMatcher dayOfWeekMatcher = _schedulingPattern.dayOfWeekMatchers.get(k);
            ValueMatcher monthMatcher = _schedulingPattern.monthMatchers.get(k);
            for (; ; ) { // day of week
                for (; ; ) { // month
                    for (; ; ) { // day of month
                        for (; ; ) { // hour
                            for (; ; ) { // minutes
                                if (minuteMatcher.match(minute)) {
                                    break;
                                }
                                minute--;
                                if (minute < 0) {
                                    minute = 59;
                                    hour--;
                                }
                            }
                            if (hour < 0) {
                                hour = 23;
                                dayOfMonth--;
                            }
                            if (hourMatcher.match(hour)) {
                                break;
                            }
                            hour--;
                            minute = 59;
                        }
                        if (dayOfMonth < 1) {
                            dayOfMonth = 31;
                            month--;
                        }
                        if (month < Calendar.JANUARY) {
                            month = Calendar.DECEMBER;
                            year--;
                        }
                        if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
                            DayOfMonthValueMatcher aux = (DayOfMonthValueMatcher) dayOfMonthMatcher;
                            if (aux.match(dayOfMonth, month + 1, c.isLeapYear(year))) {
                                break;
                            }
                            dayOfMonth--;
                            hour = 23;
                            minute = 59;
                        } else if (dayOfMonthMatcher.match(dayOfMonth)) {
                            break;
                        } else {
                            dayOfMonth--;
                            hour = 23;
                            minute = 59;
                        }
                    }
                    if (monthMatcher.match(month + 1)) {
                        break;
                    }
                    month--;
                    dayOfMonth = 31;
                    hour = 23;
                    minute = 59;
                }
                // Is this ok?
                c = new GregorianCalendar();
                c.setTimeZone(_timeZone);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.YEAR, year);
                // Day-of-month/month/year compatibility check.
                int oldDayOfMonth = dayOfMonth;
                int oldMonth = month;
                int oldYear = year;
                dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                if ((month != oldMonth) || (dayOfMonth != oldDayOfMonth) || (year != oldYear)) {
                    do {
                        dayOfMonth = oldDayOfMonth - 1;
                        month = oldMonth;
                        year = oldYear;
                        c = new GregorianCalendar();
                        c.setTimeZone(_timeZone);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.HOUR_OF_DAY, hour);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.YEAR, year);
                        // Day-of-month/month/year compatibility check.
                        oldDayOfMonth = dayOfMonth;
                        oldMonth = month;
                        oldYear = year;
                        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                        month = c.get(Calendar.MONTH);
                        year = c.get(Calendar.YEAR);

                    }
                    while ((month != oldMonth) || (dayOfMonth != oldDayOfMonth) || (year != oldYear));
                    // Take another spin!
                    continue;
                }
                // Day of week.
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
                    break;
                }
                dayOfMonth--;
                hour = 23;
                minute = 59;
                if (dayOfMonth < 1) {
                    dayOfMonth = 31;
                    month--;
                    if (month < Calendar.JANUARY) {
                        month = Calendar.DECEMBER;
                        year--;
                    }
                }
            }
            // Seems it matches!
            times[k] = (c.getTimeInMillis() / (1000 * 60)) * 1000 * 60;
        }
        // Which one?
        long min = Long.MAX_VALUE;
        for (int k = 0; k < size; k++) {
            if (times[k] < min) {
                min = times[k];
            }
        }
        // Updates the object current time value.
        _time = min;
        // Here it is.
        return _time;
    }

    /**
     * It returns the previous matching moment as a {@link Date} object.
     *
     * @return The previous matching moment as a {@link Date} object.
     */
    public synchronized Date prevMatchingDate() {
        return new Date(prevMatchingTime());
    }
}
