package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

/**
 * @author Bonux
 */
public class ConditionPlayerIsClanLeader extends Condition
{
	private final boolean _value;

	public ConditionPlayerIsClanLeader(boolean value)
	{
		_value = value;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return !_value;

		return env.character.getPlayer().isClanLeader() == _value;
	}
}
