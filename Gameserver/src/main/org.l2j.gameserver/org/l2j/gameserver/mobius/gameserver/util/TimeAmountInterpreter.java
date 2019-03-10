/*
 * Copyright (c) 1999 CERN - European Organization for Nuclear Research.
 *
 * Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in
 * supporting documentation. CERN makes no representations about the
 * suitability of this software for any purpose. It is provided "as is"
 * without expressed or implied warranty.
 */
package org.l2j.gameserver.mobius.gameserver.util;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Allows to present time intervals in a standardized, user friendly manner.
 *
 * @author _dev_
 */
public class TimeAmountInterpreter {
    private static final TimeUnit[] UNIT_CACHE = TimeUnit.values();

    private TimeAmountInterpreter() {
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
    private static String consolidate(long timeAmount, TimeUnit timeUnit) {
        return consolidate(timeAmount, timeUnit, timeUnit, DAYS, "0 " + timeUnit.name().toLowerCase(Locale.ENGLISH));
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
