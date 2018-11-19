package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Creature;
import l2s.gameserver.stats.Env;

public final class ConditionTargetHasForbiddenSkill extends Condition
{
	private final int _skillId;

	public ConditionTargetHasForbiddenSkill(int skillId)
	{
		_skillId = skillId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if(!target.isPlayable())
			return false;
		return !(target.getSkillLevel(_skillId) > 0);
	}
}
