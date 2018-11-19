package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.ResidenceType;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.stats.Env;

/**
 * @author VISTALL
 * @date 8:13/31.01.2011
 */
public class ConditionPlayerResidence extends Condition
{
	private final int _id;
	private final ResidenceType _type;

	public ConditionPlayerResidence(int id, ResidenceType type)
	{
		_id = id;
		_type = type;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		Player player = (Player) env.character;
		Clan clan = player.getClan();
		if(clan == null)
			return false;

		int residenceId = clan.getResidenceId(_type);

		return _id > 0 ? residenceId == _id : residenceId > 0;
	}
}
