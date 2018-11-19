package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;

public class ConditionUsingArmor extends Condition
{
	private final ArmorType _armor;

	public ConditionUsingArmor(ArmorType armor)
	{
		_armor = armor;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer() && ((Player) env.character).getWearingArmorType() == _armor)
			return true;

		return false;
	}
}
