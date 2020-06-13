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
package org.l2j.gameserver.model.item.auction;

import org.l2j.gameserver.model.StatsSet;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Forsaiken
 */
public final class AuctionDateGenerator {
    public static final String FIELD_INTERVAL = "interval";
    public static final String FIELD_DAY_OF_WEEK = "day_of_week";
    public static final String FIELD_HOUR_OF_DAY = "hour_of_day";
    public static final String FIELD_MINUTE_OF_HOUR = "minute_of_hour";

    private static final long MILLIS_IN_WEEK = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);

    private final Calendar _calendar;

    private final int _interval;
    private int _day_of_week;
    private int _hour_of_day;
    private int _minute_of_hour;

    public AuctionDateGenerator(StatsSet config) throws IllegalArgumentException {
        _calendar = Calendar.getInstance();
        _interval = config.getInt(FIELD_INTERVAL, -1);
        // week start in Monday.
        final int fixedDayWeek = config.getInt(FIELD_DAY_OF_WEEK, -1) + 1;
        _day_of_week = (fixedDayWeek > 7) ? 1 : fixedDayWeek;
        _hour_of_day = config.getInt(FIELD_HOUR_OF_DAY, -1);
        _minute_of_hour = config.getInt(FIELD_MINUTE_OF_HOUR, -1);

        checkDayOfWeek(-1);
        checkHourOfDay(-1);
        checkMinuteOfHour(0);
    }

    public final synchronized long nextDate(long date) {
        _calendar.setTimeInMillis(date);
        _calendar.set(Calendar.MILLISECOND, 0);
        _calendar.set(Calendar.SECOND, 0);

        _calendar.set(Calendar.MINUTE, _minute_of_hour);
        _calendar.set(Calendar.HOUR_OF_DAY, _hour_of_day);
        if (_day_of_week > 0) {
            _calendar.set(Calendar.DAY_OF_WEEK, _day_of_week);
            return calcDestTime(_calendar.getTimeInMillis(), date, MILLIS_IN_WEEK);
        }

        return calcDestTime(_calendar.getTimeInMillis(), date, TimeUnit.MILLISECONDS.convert(_interval, TimeUnit.DAYS));
    }

    private long calcDestTime(long time, long date, long add) {
        if (time < date) {
            time += ((date - time) / add) * add;
            if (time < date) {
                time += add;
            }
        }
        return time;
    }

    private void checkDayOfWeek(int defaultValue) {
        if ((_day_of_week < 1) || (_day_of_week > 7)) {
            if ((defaultValue == -1) && (_interval < 1)) {
                throw new IllegalArgumentException("Illegal params for '" + FIELD_DAY_OF_WEEK + "': " + (_day_of_week == -1 ? "not found" : _day_of_week));
            }
            _day_of_week = defaultValue;
        } else if (_interval > 1) {
            throw new IllegalArgumentException("Illegal params for '" + FIELD_INTERVAL + "' and '" + FIELD_DAY_OF_WEEK + "': you can use only one, not both");
        }
    }

    private void checkHourOfDay(int defaultValue) {
        if ((_hour_of_day < 0) || (_hour_of_day > 23)) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException("Illegal params for '" + FIELD_HOUR_OF_DAY + "': " + (_hour_of_day == -1 ? "not found" : _hour_of_day));
            }
            _hour_of_day = defaultValue;
        }
    }

    private void checkMinuteOfHour(int defaultValue) {
        if ((_minute_of_hour < 0) || (_minute_of_hour > 59)) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException("Illegal params for '" + FIELD_MINUTE_OF_HOUR + "': " + (_minute_of_hour == -1 ? "not found" : _minute_of_hour));
            }
            _minute_of_hour = defaultValue;
        }
    }
}