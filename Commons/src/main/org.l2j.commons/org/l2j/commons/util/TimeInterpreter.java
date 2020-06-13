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

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Allows to present time intervals in a standardized, user friendly manner.
 *
 * @author _dev_
 * @author JoeAlisson
 */
public class TimeInterpreter {
    private static final TimeUnit[] UNIT_CACHE = TimeUnit.values();

    private TimeInterpreter() {
        // utility class
    }

    /**
     * Calls {@link #consolidate(long, TimeUnit)} with {@link TimeUnit#MILLISECONDS}.
     *
     * @param timeAmountInMillis amount of time in milliseconds
     * @return an user-friendly description of the given time amount
     */
    public static String consolidateMillis(long timeAmountInMillis) {
        return consolidate(timeAmountInMillis, MILLISECONDS);
    }

    /**
     * Constructs an user-friendly description of the given amount of time, specifying the number of days (if any), hours (if any), minutes (if any), seconds (if any) and milliseconds (if any). Otherwise, returns the string value {@code 0}.
     *
     * @param timeAmount amount of time to be written
     * @param timeUnit   unit of the given amount
     * @return an user-friendly description of the given time amount
     */
    public static String consolidate(long timeAmount, TimeUnit timeUnit) {
        return consolidate(timeAmount, timeUnit, timeUnit, DAYS, "0 " + timeUnit.name().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Constructs an user-friendly description of the given amount of time, specifying the number of days (if any), hours (if any), minutes (if any), seconds (if any) and milliseconds (if any). Otherwise, returns the string value {@code 0}.
     *
     * @param timeAmount amount of time to be written
     * @param timeUnit   unit of the given amount
     * @return an user-friendly description of the given time amount
     */
    public static String consolidate(long timeAmount, TimeUnit timeUnit, TimeUnit minUnit) {
        return consolidate(timeAmount, timeUnit, minUnit, DAYS, "0 " + timeUnit.name().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Constructs an user-friendly description of the given amount of time.
     *
     * @param timeAmount           amount of time to be written
     * @param timeUnit             unit of the given amount
     * @param minConsolidationUnit smallest unit to be included within the description
     * @param maxConsolidationUnit largest unit to be included within the description
     * @param noTimeUsedIndicator  text to be written if the amount is not positive
     * @return an user-friendly description of the given time amount
     */
    public static String consolidate(long timeAmount, TimeUnit timeUnit, TimeUnit minConsolidationUnit, TimeUnit maxConsolidationUnit, String noTimeUsedIndicator) {
        return appendConsolidated(new StringBuilder(), timeAmount, timeUnit, minConsolidationUnit, maxConsolidationUnit, noTimeUsedIndicator).toString();
    }

    /**
     * Appends an user-friendly description of the given amount of time to the specified text builder.<BR>
     * <BR>
     * Please keep in mind, that this method is primarily designed to be used with heap text builders. Therefore, if the given text builder throws an {@link IOException}, this exception will be wrapped in a {@link RuntimeException} and returned to the caller as an unchecked exception.
     *
     * @param <T>
     * @param textBuilder          a character sequence builder
     * @param timeAmount           amount of time to be written
     * @param timeUnit             unit of the given amount
     * @param minConsolidationUnit smallest unit to be included within the description
     * @param maxConsolidationUnit largest unit to be included within the description
     * @param noTimeUsedIndicator  text to be written if the amount is not positive
     * @return {@code textBuilder}
     * @throws RuntimeException if {@code textBuilder} throws an {@link IOException}
     */
    @SuppressWarnings("unchecked")
    private static <T extends Appendable & CharSequence> T appendConsolidated(T textBuilder, long timeAmount, TimeUnit timeUnit, TimeUnit minConsolidationUnit, TimeUnit maxConsolidationUnit, String noTimeUsedIndicator) throws RuntimeException {
        try {
            if (timeAmount < 1) {
                return (T) textBuilder.append(noTimeUsedIndicator);
            }

            final int len = textBuilder.length();
            for (int i = maxConsolidationUnit.ordinal(); i >= minConsolidationUnit.ordinal(); --i) {
                final TimeUnit activeUnit = UNIT_CACHE[i];
                final long num = activeUnit.convert(timeAmount, timeUnit);
                if (num == 0) {
                    continue;
                }

                if (textBuilder.length() > len) {
                    textBuilder.append(", ");
                }
                textBuilder.append(String.valueOf(num)).append(' ');
                final String unit = activeUnit.name().toLowerCase(Locale.ENGLISH);
                textBuilder.append(unit, 0, num == 1 ? unit.length() - 1 : unit.length());

                timeAmount -= timeUnit.convert(num, activeUnit);
            }

            if (textBuilder.length() == len) {
                return (T) textBuilder.append(noTimeUsedIndicator).append(' ').append(minConsolidationUnit.name().toLowerCase(Locale.ENGLISH));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textBuilder;
    }
}
