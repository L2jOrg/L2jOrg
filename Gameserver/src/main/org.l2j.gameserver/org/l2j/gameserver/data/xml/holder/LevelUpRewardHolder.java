package org.l2j.gameserver.data.xml.holder;

import io.github.joealisson.primitive.maps.IntLongMap;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.data.xml.AbstractHolder;

/**
 * @author Bonux
**/
public final class LevelUpRewardHolder extends AbstractHolder
{
	private static final LevelUpRewardHolder _instance = new LevelUpRewardHolder();

	private final IntObjectMap<IntLongMap> _rewardData = new HashIntObjectMap<>();

	public static LevelUpRewardHolder getInstance()
	{
		return _instance;
	}

	public void addRewardData(int level, IntLongMap items)
	{
		_rewardData.put(level, items);
	}

	public IntLongMap getRewardData(int level)
	{
		return _rewardData.get(level);
	}

	@Override
	public int size()
	{
		return _rewardData.size();
	}

	@Override
	public void clear()
	{
		_rewardData.clear();
	}
}