package l2s.gameserver.model.base;

import java.util.Arrays;

/**
 * @author VISTALL
 * @date 1:14/16.06.2011
 */
public enum TeamType
{
	NONE,
	BLUE,
	RED;

	public static TeamType[] VALUES = Arrays.copyOfRange(values(), 1, 3);

	public int ordinalWithoutNone()
	{
		return ordinal() - 1;
	}

	public TeamType revert()
	{
		return this == BLUE ? RED : this == RED ? BLUE : NONE;
	}
}