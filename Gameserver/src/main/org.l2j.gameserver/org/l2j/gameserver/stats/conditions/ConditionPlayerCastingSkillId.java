package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.stats.Env;

public class ConditionPlayerCastingSkillId extends Condition
{
	private final int _skillId;

	public ConditionPlayerCastingSkillId(int skillId)
	{
		_skillId = skillId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.getCastingSkill() != null && env.character.getCastingSkill().getId() == _skillId)
			return true;

		return false;
	}
}