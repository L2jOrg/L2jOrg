package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.stats.Env;

/**
 * @author Bonux
 */
public class ConditionPlayerClanLeaderOnline extends Condition
{
	private final boolean _value;

	public ConditionPlayerClanLeaderOnline(boolean value)
	{
		_value = value;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return !_value;

		Clan clan = env.character.getPlayer().getClan();
		if(clan == null)
			return !_value;

		return clan.getLeader().isOnline() == _value;
	}
}
