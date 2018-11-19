package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

public class RewardObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final int _itemId;
	private final long _minCount;
	private final long _maxCount;
	private final double _chance;

	public RewardObject(int itemId, long minCount, long maxCount, double chance)
	{
		_itemId = itemId;
		_minCount = minCount;
		_maxCount = maxCount;
		_chance = chance;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getMinCount()
	{
		return _minCount;
	}

	public long getMaxCount()
	{
		return _maxCount;
	}

	public double getChance()
	{
		return _chance;
	}
}