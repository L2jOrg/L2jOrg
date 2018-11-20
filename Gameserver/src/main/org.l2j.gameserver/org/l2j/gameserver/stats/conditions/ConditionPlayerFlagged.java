package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerFlagged extends Condition
{
	private final boolean _flagged;

	public ConditionPlayerFlagged(boolean flagged)
	{
		_flagged = flagged;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(_flagged)
			return ((Player) env.character).getPvpFlag() > 0;
		return ((Player) env.character).getPvpFlag() <= 0;
	}
}