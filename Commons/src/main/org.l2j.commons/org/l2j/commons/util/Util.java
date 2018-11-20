/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.util;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Util {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
            .withLocale( Locale.getDefault() )
            .withZone( ZoneId.systemDefault() );

    public static boolean isInternalIP(String ipAddress) {
        return (ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") ||
                // ipAddress.startsWith("172.16.") ||
                // Removed because there are some net IPs in this range.
                // TODO: Use regexp or something to only include 172.16.0.0 => 172.16.31.255
                ipAddress.startsWith("127.0.0.1"));
    }

    public static String printData(byte[] raw) {
        return printData(raw, raw.length);
    }

    public static String printData(byte[] data, int len) {
        StringBuilder result = new StringBuilder();

        int counter = 0;

        for (int i = 0; i < len; i++) {
            if ((counter % 16) == 0) {
                result.append(fillHex(i, 4) + ": ");
            }

            result.append(fillHex(data[i] & 0xff, 2) + " ");
            counter++;
            if (counter == 16) {
                result.append("   ");

                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    int t1 = data[charpoint++];
                    if ((t1 > 0x1f) && (t1 < 0x80)) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }

                result.append("\n");
                counter = 0;
            }
        }

        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < (17 - rest); i++) {
                result.append("   ");
            }

            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                int t1 = data[charpoint++];
                if ((t1 > 0x1f) && (t1 < 0x80)) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static String fillHex(int data, int digits) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(data));

        for (int i = builder.length(); i < digits; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    public static byte[] stringToHex(String string) {
        return new BigInteger(string, 16).toByteArray();
    }

    public static String hexToString(byte[] hex) {
        if (isNull(hex)) {
            return "null";
        }
        return new BigInteger(hex).toString(16);
    }

    public static Optional<Field> getField(String fieldName, Class<?> clazz) {
        Class<?> searchClass = clazz;
        Field f = null;
        while(nonNull(searchClass)) {
            try {
                f = searchClass.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                searchClass = searchClass.getSuperclass();
            }
        }
        return Optional.of(f);
    }

    public static boolean isNullOrEmpty(String value) {
        return isNull(value) || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return nonNull(value) && !value.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static String capitalize(String text) {
        if(isNullOrEmpty(text)){
            return "";
        }
        String[] words  = text.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String word : words) {
            char[] caracteres = word.toLowerCase().toCharArray();
            caracteres[0] = Character.toUpperCase(caracteres[0]);
            builder.append(caracteres);
            builder.append(" ");
        }
        return  builder.substring(0, builder.length()-1);
    }

    public static String formatDateTime(TemporalAccessor temporal) {
        return formatter.format(temporal);
    }

    public static String hash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] raw = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(md.digest(raw));
    }
}
