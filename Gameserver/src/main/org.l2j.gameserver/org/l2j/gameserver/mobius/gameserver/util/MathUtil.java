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
package org.l2j.gameserver.mobius.gameserver.util;

/**
 * @author UnAfraid
 */
public class MathUtil {
    public static byte add(byte oldValue, byte value) {
        return (byte) (oldValue + value);
    }

    public static short add(short oldValue, short value) {
        return (short) (oldValue + value);
    }

    public static int add(int oldValue, int value) {
        return oldValue + value;
    }

    public static double add(double oldValue, double value) {
        return oldValue + value;
    }

    public static byte mul(byte oldValue, byte value) {
        return (byte) (oldValue * value);
    }

    public static short mul(short oldValue, short value) {
        return (short) (oldValue * value);
    }

    public static int mul(int oldValue, int value) {
        return oldValue * value;
    }

    public static double mul(double oldValue, double value) {
        return oldValue * value;
    }

    public static byte div(byte oldValue, byte value) {
        return (byte) (oldValue / value);
    }

    public static short div(short oldValue, short value) {
        return (short) (oldValue / value);
    }

    public static int div(int oldValue, int value) {
        return oldValue / value;
    }

    public static double div(double oldValue, double value) {
        return oldValue / value;
    }

    /**
     * @param numToTest : The number to test.
     * @param min       : The minimum limit.
     * @param max       : The maximum limit.
     * @return the number or one of the limit (mininum / maximum).
     */
    public static int limit(int numToTest, int min, int max) {
        return (numToTest > max) ? max : ((numToTest < min) ? min : numToTest);
    }
}
