package org.l2j.commons.lang;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;

public final class ArrayUtils
{
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * Check if index is in valid range of array, if so return array value
	 *
	 * @param array
	 * @param index
	 * @return array element or null, if index out of range
	 */
	public static <T> T valid(T[] array, int index)
	{
		if(array == null)
			return null;
		if(index < 0 || array.length <= index)
			return null;
		return array[index];
	}

	/**
	 * Enlarge and add element to array
	 *
	 * @param array
	 * @param element
	 * @return new array with element
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T[] add(T[] array, T element)
	{
		Class type = array != null ? array.getClass().getComponentType() : element != null ? element.getClass() : Object.class;
		T[] newArray = (T[]) copyArrayGrow(array, type);
		newArray[newArray.length - 1] = element;
		return newArray;
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] copyArrayGrow(T[] array, Class<? extends T> type)
	{
		if(array != null)
		{
			int arrayLength = Array.getLength(array);
			T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
			System.arraycopy(array, 0, newArray, 0, arrayLength);
			return newArray;
		}
		return (T[]) Array.newInstance(type, 1);
	}

	/**
	 * Check if value is one of the elements of array
	 *
	 * @param array
	 * @param value
	 * @return true, if value is present in array
	 */
	public static <T> boolean contains(T[] array, T value)
	{
		if(array == null)
			return false;

		for(int i = 0; i < array.length; i++)
			if(value == array[i])
				return true;
		return false;
	}

	/**
	 * Check if value is one of the elements of array, from starting index, and returns it position in array
	 *
	 * @param array
	 * @param value
	 * @param index
	 * @return position of value in array, or INDEX_NOT_FOUND
	 */
	public static <T> int indexOf(T[] array, T value, int index)
	{
		if(index < 0 || array.length <= index)
			return INDEX_NOT_FOUND;

		for(int i = index; i < array.length; i++)
			if(value == array[i])
				return i;

		return INDEX_NOT_FOUND;
	}

	/**
	 * Trim and remove element from array
	 *
	 * @param array
	 * @param value
	 * @return new array without element, if it present in array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] remove(T[] array, T value)
	{
		if(array == null)
			return null;

		int index = indexOf(array, value, 0);

		if(index == INDEX_NOT_FOUND)
			return array;

		int length = array.length;

		T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), length - 1);
		System.arraycopy(array, 0, newArray, 0, index);
		if(index < length - 1)
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);

		return newArray;
	}

	public static int[] toArray(Collection<Integer> collection)
	{
		int[] ar = new int[collection.size()];
		int i = 0;
		for(Integer t : collection)
			ar[i++] = t;
		return ar;
	}

	public static int[] createAscendingArray(int min, int max)
	{
		int length = max - min;
		int[] array = new int[length + 1];
		int x = 0;
		for(int i = min; i <= max; i++, x++)
			array[x] = i;
		return array;
	}
}