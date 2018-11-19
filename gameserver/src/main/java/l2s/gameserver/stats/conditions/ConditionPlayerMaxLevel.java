package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerMaxLevel extends Condition
{
	private final int _level;

	public ConditionPlayerMaxLevel(int level)
	{
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return _level != -1 && env.character.getLevel() <= _level;
	}
}