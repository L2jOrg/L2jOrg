package l2s.gameserver.templates.item.data;

/**
 * @author Bonux
 */
public class CapsuledItemData extends RewardItemData
{
	private final int _enchantLevel;

	public CapsuledItemData(int id, long minCount, long maxCount, double chance, int enchantLevel)
	{
		super(id, minCount, maxCount, chance);
		_enchantLevel = enchantLevel;
	}

	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
}