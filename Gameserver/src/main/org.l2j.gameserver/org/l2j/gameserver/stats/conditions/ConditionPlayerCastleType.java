package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.ResidenceSide;
import org.l2j.gameserver.stats.Env;

public class ConditionPlayerCastleType extends Condition
{
	private final ResidenceSide _type;

	public ConditionPlayerCastleType(ResidenceSide type)
	{
		_type = type;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		if(env.character.getPlayer().getClan() == null)
			return false;

		Castle castle = env.character.getPlayer().getCastle();
		if(castle == null)
			return false;

		return castle.getResidenceSide() == _type;
	}
}