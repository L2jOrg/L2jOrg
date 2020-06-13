/*
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
package org.l2j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;

/**
 * @author Luis Arias
 * @author JoeAlisson
 */
public class DateRange {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateRange.class);
    public static final DateRange STARTED_DAY = new DateRange(LocalDate.now(), LocalDate.now().plusDays(1));

    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate from, LocalDate to) {
        startDate = from;
        endDate = to;
    }

    /**
     * parse to DateRange
     *
     * @param startDate String representation of initial date format ISO (yyyy-mm-dd)
     * @param endDate String representation of end date format ISO (yyyy-mm-dd)
     * @return The DataRange instance
     */
    public static DateRange parse(String startDate, String endDate) {
        try {
            return new DateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return STARTED_DAY;
    }

    public boolean isValid() {
        return startDate.isBefore(endDate);
    }

    public boolean isWithinRange(LocalDate date) {
        return startDate.isEqual(date) || endDate.isEqual(date) || (startDate.isBefore(date) && endDate.isAfter(date));
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public long millisToEnd() {
        return Duration.between(LocalDate.now(), endDate).toMillis();
    }

    public long secondsToStart(LocalDate today) {
        return Duration.between(today, startDate).toSeconds();
    }

    public boolean isAfter(LocalDate date) {
        return startDate.isAfter(date);
    }

    @Override
    public String toString() {
        return String.format("From: %s until %s", startDate, endDate);
    }
}
