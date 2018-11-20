package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerIsChaotic extends Condition
{
	private final boolean _chaotic;

	public ConditionPlayerIsChaotic(boolean chaotic)
	{
		_chaotic = chaotic;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Player player = env.character.getPlayer();
		if(player == null)
			return !_chaotic;
		if(player.isPK())
			return _chaotic;
		return !_chaotic;
	}
}