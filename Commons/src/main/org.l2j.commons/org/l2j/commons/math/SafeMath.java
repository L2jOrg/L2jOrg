package org.l2j.commons.math;

/**
 * Безопасная от переполнения математика.<br>
 * Код взят из пакета Apache commons-math.
 * 
 * @see http://commons.apache.org/math/
 */
public class SafeMath
{
	/**
	 * Сложение двух целых с проверкой на переполнение.
	 *
	 * @param a слагаемое
	 * @param b слагаемое
	 * @return сумму <code>a+b</code>
	 * @throws ArithmeticException если результат превысил диапазон целого
	 */
	public static int addAndCheck(int a, int b) throws ArithmeticException{
		return addAndCheck(a, b, "overflow: add", false);
	}

	/**
	 * Сложение двух целых с проверкой на переполнение.
	 *
	 * @param a слагаемое
	 * @param b слагаемое
	 * @return сумму <code>a+b</code>, либо граничное значение целого, в случае переполнения
	 */
	public static int addAndLimit(int a, int b){
		return addAndCheck(a, b, null, true);
	}

	private static int addAndCheck(int a, int b, String msg, boolean limit) {
		int ret;
		if (a > b)
			// use symmetry to reduce boundary cases
			ret = addAndCheck(b, a, msg, limit);
		else if (a < 0) {
			if (b < 0) {
				// check for negative overflow
				if (Integer.MIN_VALUE - b <= a)
					ret = a + b;
				else if (limit)
					ret = Integer.MIN_VALUE;
				else
					throw new ArithmeticException(msg);
			}
			else
				// opposite sign addition is always safe
				ret = a + b;
		}
		else // check for positive overflow
			if (a <= Integer.MAX_VALUE - b)
				ret = a + b;
			else if (limit)
				ret = Integer.MAX_VALUE;
			else
				throw new ArithmeticException(msg);
		return ret;
	}

	/**
	 * Сложение двух целых с проверкой на переполнение.
	 *
	 * @param a слагаемое
	 * @param b слагаемое
	 * @return сумму <code>a+b</code>, либо граничное значение целого, в случае переполнения
	 */
	public static long addAndLimit(long a, long b) {
		return addAndCheck(a, b, "overflow: add", true);
	}

	/**
	 * Сложение двух целых с проверкой на переполнение.
	 *
	 * @param a слагаемое
	 * @param b слагаемое
	 * @return сумму <code>a+b</code>
	 * @throws ArithmeticException если результат превысил диапазон целого
	 */
	public static long addAndCheck(long a, long b) throws ArithmeticException {
		return addAndCheck(a, b, "overflow: add", false);
	}

	private static long addAndCheck(long a, long b, String msg, boolean limit) {
		long ret;
		if (a > b)
			// use symmetry to reduce boundary cases
			ret = addAndCheck(b, a, msg, limit);
		else if (a < 0) {
			if (b < 0) {
				// check for negative overflow
				if (Long.MIN_VALUE - b <= a)
					ret = a + b;
				else if (limit)
					ret = Long.MIN_VALUE;
				else
					throw new ArithmeticException(msg);
			}
			else
				// opposite sign addition is always safe
				ret = a + b;
		}
		else // check for positive overflow
			if (a <= Long.MAX_VALUE - b)
				ret = a + b;
			else if (limit)
				ret = Long.MAX_VALUE;
			else
				throw new ArithmeticException(msg);
		return ret;
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>
	 * @throws ArithmeticException если результат превысил диапазон целого
	 */
	public static int mulAndCheck(int a, int b)  throws ArithmeticException
	{
		return mulAndCheck(a, b, "overflow: mul", false);
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>, либо граничное значение целого, в случае переполнения
	 */
	public static int mulAndLimit(int a, int b)
	{
		return mulAndCheck(a, b, "overflow: mul", true);
	}

	private static int mulAndCheck(int a, int b, String msg, boolean limit) {
		int ret;
		if (a > b)
			// use symmetry to reduce boundary cases
			ret = mulAndCheck(b, a, msg, limit);
		else if (a < 0) {
			if (b < 0) {
				// check for positive overflow with negative a, negative b
				if (a >= Integer.MAX_VALUE / b)
					ret = a * b;
				else if(limit)
					ret = Integer.MAX_VALUE;
				else
					throw new ArithmeticException(msg);
			} else if (b > 0) {
				// check for negative overflow with negative a, positive b
				if (Integer.MIN_VALUE / b <= a)
					ret = a * b;
				else if(limit)
					ret = Integer.MIN_VALUE;
				else
					throw new ArithmeticException(msg);
			}
			else
				ret = 0;
		} else if (a > 0) {
			// check for positive overflow with positive a, positive b
			if (a <= Integer.MAX_VALUE / b)
				ret = a * b;
			else if(limit)
				ret = Integer.MAX_VALUE;
			else
				throw new ArithmeticException(msg);
		}
		else
			ret = 0;
		return ret;
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>
	 * @throws ArithmeticException если результат превысил диапазон целого
	 */
	public static long mulAndCheck(long a, long b)  throws ArithmeticException
	{
		return mulAndCheck(a, b, "overflow: mul", false);
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>, либо граничное значение целого, в случае переполнения
	 */
	public static long mulAndLimit(long a, long b)
	{
		return mulAndCheck(a, b, "overflow: mul", true);
	}

	private static long mulAndCheck(long a, long b, String msg, boolean limit) {
		long ret;
		if (a > b)
			// use symmetry to reduce boundary cases
			ret = mulAndCheck(b, a, msg, limit);
		else if (a < 0) {
			if (b < 0) {
				// check for positive overflow with negative a, negative b
				if (a >= Long.MAX_VALUE / b)
					ret = a * b;
				else if(limit)
					ret = Long.MAX_VALUE;
				else
					throw new ArithmeticException(msg);
			} else if (b > 0) {
				// check for negative overflow with negative a, positive b
				if (Long.MIN_VALUE / b <= a)
					ret = a * b;
				else if(limit)
					ret = Long.MIN_VALUE;
				else
					throw new ArithmeticException(msg);
			}
			else
				ret = 0;
		} else if (a > 0) {
			// check for positive overflow with positive a, positive b
			if (a <= Long.MAX_VALUE / b)
				ret = a * b;
			else if(limit)
				ret = Long.MAX_VALUE;
			else
				throw new ArithmeticException(msg);
		}
		else
			ret = 0;
		return ret;
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>
	 * @throws ArithmeticException если результат превысил диапазон целого
	 */
	public static double mulAndCheck(double a, double b) throws ArithmeticException
	{
		return mulAndCheck(a, b, "overflow: mul", false);
	}

	/**
	 * Умножение двух целых с проверкой на переполнение.
	 *
	 * @param a множитель
	 * @param b множитель
	 * @return произведение <code>a*b</code>, либо граничное значение целого, в случае переполнения
	 */
	public static double mulAndLimit(double a, double b)
	{
		return mulAndCheck(a, b, "overflow: mul", true);
	}

	private static double mulAndCheck(double a, double b, String msg, boolean limit) {
		double ret;
		if (a > b)
			// use symmetry to reduce boundary cases
			ret = mulAndCheck(b, a, msg, limit);
		else if (a < 0) {
			if (b < 0) {
				// check for positive overflow with negative a, negative b
				if (a >= Double.MAX_VALUE / b)
					ret = a * b;
				else if(limit)
					ret = Double.MAX_VALUE;
				else
					throw new ArithmeticException(msg);
			} else if (b > 0) {
				// check for negative overflow with negative a, positive b
				if (Double.MIN_VALUE / b <= a)
					ret = a * b;
				else if(limit)
					ret = Double.MIN_VALUE;
				else
					throw new ArithmeticException(msg);
			}
			else
				ret = 0;
		} else if (a > 0) {
			// check for positive overflow with positive a, positive b
			if (a <= Double.MAX_VALUE / b)
				ret = a * b;
			else if(limit)
				ret = Double.MAX_VALUE;
			else
				throw new ArithmeticException(msg);
		}
		else
			ret = 0;
		return ret;
	}
}