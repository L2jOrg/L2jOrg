package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.BaseStatsBonus;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class BaseStatsBonusHolder extends AbstractHolder
{
	private static final BaseStatsBonusHolder _instance = new BaseStatsBonusHolder();

	private final IntObjectMap<BaseStatsBonus> _bonuses = new HashIntObjectMap<BaseStatsBonus>();

	public static BaseStatsBonusHolder getInstance()
	{
		return _instance;
	}

	public void addBaseStatsBonus(int value, BaseStatsBonus bonus)
	{
		_bonuses.put(value, bonus);
	}

	public BaseStatsBonus getBaseStatsBonus(int value)
	{
		return _bonuses.get(value);
	}

	@Override
	public int size()
	{
		return _bonuses.size();
	}

	@Override
	public void clear()
	{
		_bonuses.clear();
	}
}