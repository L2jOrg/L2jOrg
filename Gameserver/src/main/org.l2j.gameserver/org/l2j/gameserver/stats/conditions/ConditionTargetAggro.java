package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.stats.Env;

public class ConditionTargetAggro extends Condition
{
	private final boolean _isAggro;

	public ConditionTargetAggro(boolean isAggro)
	{
		_isAggro = isAggro;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if(target == null)
			return false;
		if(target.isMonster())
			return ((MonsterInstance) target).isAggressive() == _isAggro;
		if(target.isPlayer())
			return target.isPK();
		return false;
	}
}
