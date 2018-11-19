package l2s.gameserver.templates.premiumaccount;

public class PremiumAccountModifiers
{
	private final double _dropChance;
	private final double _spoilChance;

	public PremiumAccountModifiers(double dropChance, double spoilChance)
	{
		_dropChance = dropChance;
		_spoilChance = spoilChance;
	}

	public double getDropChance()
	{
		return _dropChance;
	}

	public double getSpoilChance()
	{
		return _spoilChance;
	}
}