package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.SummonInstance;
import org.l2j.gameserver.stats.Env;

/**
 * @author Bonux
 */
public class ConditionPlayerHasSummonId extends Condition
{
	private int _id;

	public ConditionPlayerHasSummonId(int id)
	{
		_id = id;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.target == null || !env.target.isPlayer())
			return false;

		Player player = (Player) env.target;
		SummonInstance summon = player.getSummon();
		return summon != null && summon.getNpcId() == _id;
	}
}