package l2s.gameserver.templates.premiumaccount;

/**
 * @author Bonux
 **/
public class PremiumAccountBonus
{
	private final double _enchantChance;
	private final double _craftChance;

	public PremiumAccountBonus(double enchantChance, double craftChance)
	{
		_enchantChance = enchantChance;
		_craftChance = craftChance;
	}

	public double getEnchantChance()
	{
		return _enchantChance;
	}

	public double getCraftChance()
	{
		return _craftChance;
	}
}