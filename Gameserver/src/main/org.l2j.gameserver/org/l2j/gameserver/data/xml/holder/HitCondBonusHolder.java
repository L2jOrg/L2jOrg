package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.HitCondBonusType;

/**
 * @author Bonux
**/
public final class HitCondBonusHolder extends AbstractHolder
{
	private static final HitCondBonusHolder _instance = new HitCondBonusHolder();

	private final TIntDoubleMap _bonusList = new TIntDoubleHashMap();

	public static HitCondBonusHolder getInstance()
	{
		return _instance;
	}

	public void addHitCondBonus(HitCondBonusType type, double value)
	{
		_bonusList.put(type.ordinal(), value);
	}

	public double getHitCondBonus(HitCondBonusType type)
	{
		return _bonusList.get(type.ordinal());
	}

	@Override
	public int size()
	{
		return _bonusList.size();
	}

	@Override
	public void clear()
	{
		_bonusList.clear();
	}
}