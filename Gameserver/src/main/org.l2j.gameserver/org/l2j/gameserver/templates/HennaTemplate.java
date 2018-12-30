package org.l2j.gameserver.templates;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.sets.IntSet;
import org.l2j.gameserver.model.Player;

/**
 * Reworked by VISTALL
 */
public class HennaTemplate
{
	private final int _symbolId;
	private final int _dyeId;
	private final int _dyeLvl;
	private final long _drawPrice;
	private final long _drawCount;
	private final long _removePrice;
	private final long _removeCount;
	private final int _statINT;
	private final int _statSTR;
	private final int _statCON;
	private final int _statMEN;
	private final int _statDEX;
	private final int _statWIT;
	private final IntSet _classes;
	private final IntIntMap _skills;
	private final int _period;

	public HennaTemplate(int symbolId, int dyeId, int dyeLvl, long drawPrice, long drawCount, long removePrice, long removeCount, int wit, int intA, int con, int str, int dex, int men, IntSet classes, IntIntMap skills, int period)
	{
		_symbolId = symbolId;
		_dyeId = dyeId;
		_dyeLvl = dyeLvl;
		_drawPrice = drawPrice;
		_drawCount = drawCount;
		_removePrice = removePrice;
		_removeCount = removeCount;
		_statINT = intA;
		_statSTR = str;
		_statCON = con;
		_statMEN = men;
		_statDEX = dex;
		_statWIT = wit;
		_classes = classes;
		_skills = skills;
		_period = period;
	}

	public int getSymbolId()
	{
		return _symbolId;
	}

	public int getDyeId()
	{
		return _dyeId;
	}

	public int getDyeLvl()
	{
		return _dyeLvl;
	}

	public long getDrawPrice()
	{
		return _drawPrice;
	}

	public long getDrawCount()
	{
		return _drawCount;
	}

	public long getRemovePrice()
	{
		return _removePrice;
	}

	public long getRemoveCount()
	{
		return _removeCount;
	}

	public int getStatINT()
	{
		return _statINT;
	}

	public int getStatSTR()
	{
		return _statSTR;
	}

	public int getStatCON()
	{
		return _statCON;
	}

	public int getStatMEN()
	{
		return _statMEN;
	}

	public int getStatDEX()
	{
		return _statDEX;
	}

	public int getStatWIT()
	{
		return _statWIT;
	}

	public boolean isForThisClass(Player player)
	{
		return _classes.contains(player.getActiveClassId());
	}

	public IntIntMap getSkills()
	{
		return _skills;
	}

	public int getPeriod()
	{
		return _period;
	}
}