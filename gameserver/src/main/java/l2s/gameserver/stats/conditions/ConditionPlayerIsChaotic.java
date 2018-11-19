package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

public class ConditionPlayerIsChaotic extends Condition
{
	private final boolean _chaotic;

	public ConditionPlayerIsChaotic(boolean chaotic)
	{
		_chaotic = chaotic;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Player player = env.character.getPlayer();
		if(player == null)
			return !_chaotic;
		if(player.isPK())
			return _chaotic;
		return !_chaotic;
	}
}