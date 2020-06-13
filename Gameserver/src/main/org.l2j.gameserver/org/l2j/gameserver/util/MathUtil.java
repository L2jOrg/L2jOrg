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
package org.l2j.gameserver.util;

import org.l2j.gameserver.model.interfaces.ILocational;

import static java.lang.Math.pow;

/**
 * @author UnAfraid
 * @author joeAlisson
 */
public final class MathUtil {

    private MathUtil() {

    }

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
        return (numToTest > max) ? max : Math.max(numToTest, min);
    }

    public static boolean isInsideRadius2D(ILocational object, ILocational other, int radius) {
        return calculateDistanceSq2D(object, other) <= radius * radius;
    }

    public static boolean isInsideRadius2D(ILocational object, int x, int y, int radius) {
        return calculateDistanceSq2D(object.getX(), object.getY(), x, y) <= radius * radius;
    }

    public static  double calculateDistanceSq2D(ILocational object, ILocational other) {
        return calculateDistanceSq2D(object.getX(), object.getY(), other.getX(), other.getY());
    }

    public static double calculateDistanceSq2D(int x1, int y1, int x2, int y2) {
        return pow(x1 - x2, 2) + pow(y1 - y2, 2);
    }

    public static double calculateDistance2D(ILocational loc, ILocational other) {
        return calculateDistance2D(loc.getX(), loc.getY(), other.getX(), other.getY());
    }

    public static double calculateDistance2D(int x1, int y1, int x2, int y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    public static double calculateDistance3D(ILocational object, ILocational other) {
        return calculateDistance3D(object.getX(), object.getY(), object.getZ(), other.getX(), other.getY(), other.getZ());
    }

    public static double calculateDistance3D(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2) + pow(z1 - z2, 2));
    }

    public static double calculateDistanceSq3D(int x1, int y1, int z1, int x2, int y2, int z2) {
        return pow(x1 -x2, 2) + pow(y1 - y2, 2) + pow(z1 - z2, 2);
    }

    public static double calculateDistanceSq3D(ILocational object, ILocational other) {
        return calculateDistanceSq3D(object.getX(), object.getY(), object.getZ(), other.getX(), other.getY(), other.getZ());
    }

    public static boolean isInsideRadius3D(ILocational object, ILocational other, int range) {
        return calculateDistanceSq3D(object, other) <= range * range;
    }

    public static boolean isInsideRadius3D(ILocational object, int x, int y, int z, int range) {
        return calculateDistanceSq3D(object.getX(), object.getY(), object.getZ(), x, y, z) <= range * range;
    }

    public static boolean isInsideRadius3D(int originX, int originY, int originZ, int targetX, int targetY, int targetZ, int range) {
        return calculateDistanceSq3D(originX, originY, originZ, targetX, targetY, targetZ) <= range * range;
    }

    public static int calculateHeadingFrom(ILocational from, ILocational to) {
        return calculateHeadingFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static int calculateHeadingFrom(int fromX, int fromY, int toX, int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return (int) (angleTarget * 182.044444444);
    }

    public static int calculateHeadingFrom(double dx, double dy) {
        double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return (int) (angleTarget * 182.044444444);
    }

    public static double convertHeadingToDegree(int clientHeading) {
        return clientHeading / 182.044444444;
    }

    public static double calculateAngleFrom(ILocational from, ILocational to) {
        return calculateAngleFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static double calculateAngleFrom(int fromX, int fromY, int toX, int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return angleTarget;
    }


}
