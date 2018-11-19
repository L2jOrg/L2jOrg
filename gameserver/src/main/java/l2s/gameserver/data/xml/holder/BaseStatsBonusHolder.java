package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.BaseStatsBonus;

/**
 * @author Bonux
**/
public final class BaseStatsBonusHolder extends AbstractHolder
{
	private static final BaseStatsBonusHolder _instance = new BaseStatsBonusHolder();

	private final TIntObjectMap<BaseStatsBonus> _bonuses = new TIntObjectHashMap<BaseStatsBonus>();

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