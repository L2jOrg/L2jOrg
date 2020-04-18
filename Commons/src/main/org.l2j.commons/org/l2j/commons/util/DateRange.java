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
