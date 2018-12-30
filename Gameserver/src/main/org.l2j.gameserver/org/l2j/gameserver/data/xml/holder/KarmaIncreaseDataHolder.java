package org.l2j.gameserver.data.xml.holder;

import io.github.joealisson.primitive.maps.IntDoubleMap;
import io.github.joealisson.primitive.maps.impl.HashIntDoubleMap;
import org.l2j.commons.data.xml.AbstractHolder;

/**
 * @author Bonux
**/
public final class KarmaIncreaseDataHolder extends AbstractHolder
{
	private static final KarmaIncreaseDataHolder _instance = new KarmaIncreaseDataHolder();

	private final IntDoubleMap _bonusList = new HashIntDoubleMap();

	public static KarmaIncreaseDataHolder getInstance()
	{
		return _instance;
	}

	public void addData(int lvl, double bonus)
	{
		_bonusList.put(lvl, bonus);
	}

	public double getData(int lvl)
	{
		return _bonusList.get(lvl);
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