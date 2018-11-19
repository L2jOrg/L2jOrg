package l2s.gameserver.templates.item.data;

/**
 * @author Bonux
 */
public class ChancedItemData extends ItemData
{
	private final double _chance;

	public ChancedItemData(int id, long count, double chance)
	{
		super(id, count);
		_chance = chance;
	}

	public double getChance()
	{
		return _chance;
	}
}