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
	public static int get(int n)
	{
		return rnd().nextInt(n);
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
	 * Рандомайзер для подсчета шансов.<br>
	 * Рекомендуется к использованию вместо Rnd.get()
	 * @param chance в процентах от 0 до 100
	 * @return true в случае успешного выпадания.
	 * <li>Если chance <= 0, вернет false
	 * <li>Если chance >= 100, вернет true
	 */
	public static boolean chance(int chance)
	{
		return chance >= 1 && (chance > 99 || rnd().nextInt(99) + 1 <= chance);
	}

	/**
	 * Рандомайзер для подсчета шансов.<br>
	 * Рекомендуется к использованию вместо Rnd.get() если нужны очень маленькие шансы
	 * @param chance в процентах от 0 до 100
	 * @return true в случае успешного выпадания.
	 * <li>Если chance <= 0, вернет false
	 * <li>Если chance >= 100, вернет true
	 */
	public static boolean chance(double chance)
	{
		return rnd().nextDouble() <= chance / 100.;
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