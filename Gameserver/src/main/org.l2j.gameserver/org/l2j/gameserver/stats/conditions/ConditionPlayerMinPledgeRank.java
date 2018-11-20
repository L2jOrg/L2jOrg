package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.base.PledgeRank;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerMinPledgeRank extends Condition
{
	private final PledgeRank _rank;

	public ConditionPlayerMinPledgeRank(PledgeRank rank)
	{
		_rank = rank;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		return env.character.getPlayer().getPledgeRank().ordinal() >= _rank.ordinal();
	}
}