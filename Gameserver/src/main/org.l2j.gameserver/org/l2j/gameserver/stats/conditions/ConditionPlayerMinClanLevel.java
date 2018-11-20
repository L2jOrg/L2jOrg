package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.stats.Env;

/**
 * @author Bonux
 */
public class ConditionPlayerMinClanLevel extends Condition
{
	private final int _value;

	public ConditionPlayerMinClanLevel(int value)
	{
		_value = value;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return _value <= 0;

		Clan clan = env.character.getPlayer().getClan();
		if(clan == null)
			return _value <= 0;

		return _value <= clan.getLevel();
	}
}
