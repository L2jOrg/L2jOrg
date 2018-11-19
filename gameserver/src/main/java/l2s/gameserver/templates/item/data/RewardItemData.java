package l2s.gameserver.templates.item.data;

/**
 * @author Bonux
 */
public class RewardItemData extends ChancedItemData
{
	private final long _maxCount;

	public RewardItemData(int id, long minCount, long maxCount, double chance)
	{
		super(id, minCount, chance);
		_maxCount = maxCount;
	}

	public long getMinCount()
	{
		return getCount();
	}

	public long getMaxCount()
	{
		return _maxCount;
	}
}