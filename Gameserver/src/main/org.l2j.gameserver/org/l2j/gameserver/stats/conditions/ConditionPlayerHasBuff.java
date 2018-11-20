package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.skills.AbnormalType;
import org.l2j.gameserver.stats.Env;

public final class ConditionPlayerHasBuff extends Condition
{
	private final AbnormalType _abnormalType;
	private final int _level;

	public ConditionPlayerHasBuff(AbnormalType abnormalType, int level)
	{
		_abnormalType = abnormalType;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature character = env.character;
		if(character == null)
			return false;

		for(Abnormal effect : character.getAbnormalList())
		{
			if(effect.getAbnormalType() != _abnormalType)
				continue;

			if(_level == -1)
				return true;

			if(effect.getAbnormalLvl() >= _level)
				return true;
		}
		return false;
	}
}