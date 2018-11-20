package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.SubClassType;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerClassType extends Condition
{
	private final SubClassType _type;

	public ConditionPlayerClassType(SubClassType type)
	{
		_type = type;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();
		return player.getActiveSubClass().getType() == _type;
	}
}