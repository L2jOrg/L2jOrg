package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerMaxSP extends Condition
{
	private final int _spToAdd;

	public ConditionPlayerMaxSP(int spToAdd)
	{
		_spToAdd = spToAdd;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		long sp = env.character.getPlayer().getSp() + _spToAdd;
		return sp <= Config.SP_LIMIT;
	}
}