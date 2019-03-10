/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luis Arias
 */
public class DateRange {
    protected static final Logger LOGGER = Logger.getLogger(DateRange.class.getName());
    private final Date _startDate;
    private final Date _endDate;

    public DateRange(Date from, Date to) {
        _startDate = from;
        _endDate = to;
    }

    public static DateRange parse(String dateRange, DateFormat format) {
        final String[] date = dateRange.split("-");
        if (date.length == 2) {
            try {
                return new DateRange(format.parse(date[0]), format.parse(date[1]));
            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, "Invalid Date Format.", e);
            }
        }
        return new DateRange(null, null);
    }

    public boolean isValid() {
        return (_startDate != null) && (_endDate != null) && _startDate.before(_endDate);
    }

    public boolean isWithinRange(Date date) {
        return (date.equals(_startDate) || date.after(_startDate)) //
                && (date.equals(_endDate) || date.before(_endDate));
    }

    public Date getEndDate() {
        return _endDate;
    }

    public Date getStartDate() {
        return _startDate;
    }

    @Override
    public String toString() {
        return "DateRange: From: " + _startDate + " To: " + _endDate;
    }
}
