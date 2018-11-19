package l2s.gameserver.templates.item.support.variation;

/**
 * @author Bonux
 */
public class VariationOption
{
	private final int _id;
	private final double _chance;

	public VariationOption(int id, double chance)
	{
		_id = id;
		_chance = chance;
	}

	public int getId()
	{
		return _id;
	}

	public double getChance()
	{
		return _chance;
	}
}