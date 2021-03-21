/*
 * Copyright © 2019-2021 L2JOrg
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

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Rnd {

    private static final SecureRandom secure = new SecureRandom();

    private Rnd() {
    }

    private static Random rnd() {
        return ThreadLocalRandom.current();
    }

    /**
     * Gets a random number from 0 (inclusive) to n (exclusive)
     *
     * @param n The superior limit (exclusive)
     * @return A number from 0 to n-1
     */
    public static int get(int n) {
        return n < 1 ? 0 : rnd().nextInt(n);
    }

    /**
     * Gets a random number from 0 (inclusive) to n (exclusive)
     *
     * @param n The superior limit (exclusive)
     * @return A number from 0 to n-1
     */
    public static long get(long n) {
        return (long) (n < 1 ? 0 : rnd().nextDouble() * n);
    }

    /**
     * Gets a random number from min (inclusive) to max (inclusive)
     *
     * @param min The min value (inclusive)
     * @param max The superior limit (inclusive)
     * @return A number from min to max
     */
    public static int get(int min, int max) {
        if (min == max) {
            return min;
        }
        return min + get(max - min + 1);
    }

    /**
     * Gets a random number from min (inclusive) to max (inclusive)
     *
     * @param min The min value (inclusive)
     * @param max The superior limit (inclusive)
     * @return A number from min to max
     */
    public static long get(long min, long max) {
        if (min == max) {
            return min;
        }
        return min + get(max - min + 1);
    }

    /**
     * Gets a random number from min (inclusive) to max (inclusive)
     *
     * @param min The min value (inclusive)
     * @param max The superior limit (inclusive)
     * @return A number from min to max
     */
    public static double get(double min, double max) {
        if (min == max) {
            return min;
        }
        return min + (((max - min) + 1) * rnd().nextDouble());
    }

    /**
     * @return A random int
     */
    public static int nextInt() {
        return rnd().nextInt();
    }

    /**
     * @return A random number between 0 (inclusive) and 1.0 (exclusive)
     */
    public static double nextDouble() {
        return rnd().nextDouble();
    }

    /**
     * @return A Gaussian ("normally") distributed
     * {@code double} value with mean {@code 0.0} and
     * standard deviation {@code 1.0} from this random number
     * generator's sequence
     */
    public static double nextGaussian() {
        return rnd().nextGaussian();
    }

    /**
     * @return A random boolean
     */
    public static boolean nextBoolean() {
        return rnd().nextBoolean();
    }


    /**
     * Generates random bytes and places them into a user-supplied byte array. The number of random bytes produced is equal to the length of the byte array.
     *
     * @param bytes the byte array to fill with random bytes.
     */
    public static void nextBytes(byte[] bytes) {
        rnd().nextBytes(bytes);
    }

    /**
     * get a random chance <br>
     *
     * @param chance in percent from 0 to 100
     * @return true if successful.
     * <li> If chance <= 0, returns false
     * <li> If chance >= 100, returns true
     */
    public static boolean chance(int chance) {
        return chance >= 1 && (chance > 99 || rnd().nextInt(100) <= chance);
    }

    /**
     * get a random chance <br>
     *
     * @param chance in percent from 0 to 100
     * @return true if successful.
     *
     * <li> If chance <= 0, returns false
     * <li> If chance> = 100, returns true
     */
    public static boolean chance(double chance) {
        return chance > 0 && (chance >= 100 || rnd().nextDouble() * 100 <= chance);
    }


    public static int nextSecureInt() {
		return secure.nextInt();
    }


    public static <E> E get(E[] list) {
        if (list.length == 0)
            return null;
        if (list.length == 1)
            return list[0];
        return list[get(list.length)];
    }

    public static int get(int[] list) {
        return list[get(list.length)];
    }

    public static <E> E get(List<E> list) {
        if (list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        return list.get(get(list.size()));
    }
}