package l2s.gameserver.stats.conditions;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

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