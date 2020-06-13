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

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Rnd
{
	private Rnd() {}

	private static Random rnd() {
		return ThreadLocalRandom.current();
	}

	public static double get() // get random number from 0 to 1
	{
		return rnd().nextDouble();
	}

	/**
	 * Gets a random number from 0(inclusive) to n(exclusive)
	 *
	 * @param n The superior limit (exclusive)
	 * @return A number from 0 to n-1
	 */
	public static int get(int n) {
		return n < 1 ? 0 : rnd().nextInt(n);
	}

	public static long get(long n)
	{
		return (long) (rnd().nextDouble() * n);
	}

	public static int get(int min, int max) // get random number from min to max (not max-1 !)
	{
		return min + get(max - min + 1);
	}

	public static long get(long min, long max) // get random number from min to max (not max-1 !)
	{
		return min + get(max - min + 1);
	}

	/**
	 * @param origin (double)
	 * @param bound (double)
	 * @return a random double value between the specified origin (inclusive) and the specified bound (inclusive).
	 */
	public static double get(double origin, double bound) {
		if (origin == bound) {
			return origin;
		}
		return origin + (((bound - origin) + 1) * rnd().nextDouble());
	}

	public static int nextInt()
	{
		return rnd().nextInt();
	}

	public static double nextDouble()
	{
		return rnd().nextDouble();
	}

	public static double nextGaussian()
	{
		return rnd().nextGaussian();
	}

	public static boolean nextBoolean()
	{
		return rnd().nextBoolean();
	}


	/**
	 * Generates random bytes and places them into a user-supplied byte array. The number of random bytes produced is equal to the length of the byte array.
	 * @param bytes the byte array to fill with random bytes.
	 */
	public static void nextBytes(byte[] bytes)
	{
		rnd().nextBytes(bytes);
	}

	/**
	 * Randomizer for calculating odds. <br>
	 * Recommended for use instead of Rnd.get ()
	 * @param chance in percent from 0 to 100
	 * @return true if successful.
	 * <li> If chance <= 0, returns false
	 * <li> If chance> = 100, returns true
	 * Translated by Google.
	 */
	public static boolean chance(int chance) {
		return chance >= 1 && (chance > 99 || rnd().nextInt(100) <= chance);
	}

	/**
	 * Randomizer for calculating odds. <br>
	 * Recommended for use instead of Rnd.get () if very small chances are needed
	 * @param chance in percent from 0 to 100
	 * @return true if successful.
	 * <li> If chance <= 0, returns false
	 * <li> If chance> = 100, returns true
	 * Translated by Google.
	 */
	public static boolean chance(double chance) {
		return chance > 0 && (chance >= 100 || rnd().nextDouble() * 100 <= chance);
	}

	public static <E> E get(E[] list)
	{
		if(list.length == 0)
			return null;
		if(list.length == 1)
			return list[0];
		return list[get(list.length)];
	}

	public static int get(int[] list)
	{
		return list[get(list.length)];
	}

	public static <E> E get(List<E> list)
	{
		if(list.isEmpty())
			return null;
		if(list.size() == 1)
			return list.get(0);
		return list.get(get(list.size()));
	}
}