package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.stats.Env;

/**
 * @author Bonux
**/
public class ConditionTargetMinDistance extends Condition
{
	private final int _distance;

	public ConditionTargetMinDistance(int distance)
	{
		_distance = distance;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if(target == null)
			return false;

		return !env.character.isInRange(target.getLoc(), _distance);
	}
}
