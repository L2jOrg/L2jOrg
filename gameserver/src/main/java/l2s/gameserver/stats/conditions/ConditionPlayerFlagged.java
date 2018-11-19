package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

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