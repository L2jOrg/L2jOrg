package l2s.gameserver.data.xml.holder;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.ExperienceData;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class ExperienceDataHolder extends AbstractHolder
{
	private static final ExperienceDataHolder _instance = new ExperienceDataHolder();

	private final IntObjectMap<ExperienceData> _data = new HashIntObjectMap<ExperienceData>();

	private int _maxLevel = 0;

	public static ExperienceDataHolder getInstance()
	{
		return _instance;
	}

	public void addData(ExperienceData data)
	{
		int level = data.getLevel();
		_data.put(level, data);
		if(level > _maxLevel)
			_maxLevel = level;
	}

	public ExperienceData getData(int level)
	{
		return _data.get(level);
	}

	public boolean containsData(int level)
	{
		return _data.containsKey(level);
	}

	public int getMaxLevel()
	{
		return _maxLevel - 1;
	}

	@Override
	public int size()
	{
		return _data.size();
	}

	@Override
	public void clear()
	{
		_data.clear();
	}
}