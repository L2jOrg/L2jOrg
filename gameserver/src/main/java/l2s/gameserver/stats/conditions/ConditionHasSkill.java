package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public final class ConditionHasSkill extends Condition
{
	private final Integer _id;
	private final int _level;

	public ConditionHasSkill(Integer id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getSkillLevel(_id) >= _level;
	}
}