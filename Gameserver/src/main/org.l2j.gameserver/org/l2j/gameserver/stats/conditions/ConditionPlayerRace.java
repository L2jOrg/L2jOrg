package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerRace extends Condition
{
	private final Race _race;

	public ConditionPlayerRace(String race)
	{
		_race = Race.valueOf(race.toUpperCase());
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		return ((Player) env.character).getRace() == _race;
	}
}