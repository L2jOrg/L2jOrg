package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerSex extends Condition
{
	private final Sex _sex;

	public ConditionPlayerSex(Sex sex)
	{
		_sex = sex;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		return ((Player) env.character).getSex() == _sex;
	}
}