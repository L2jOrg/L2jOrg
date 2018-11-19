package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.stats.Env;

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