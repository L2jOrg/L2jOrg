package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

public class ConditionPlayerRiding extends Condition
{
	public enum CheckPlayerRiding
	{
		NONE,
		STRIDER,
		WYVERN
	}

	private final CheckPlayerRiding _riding;

	public ConditionPlayerRiding(CheckPlayerRiding riding)
	{
		_riding = riding;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		Player player = (Player) env.character;
		switch(_riding)
		{
			case STRIDER:
				if(player.isMounted() && !player.isFlying())
					return true;
				break;
			case WYVERN:
				if(player.isMounted() && player.isFlying())
					return true;
				break;
			case NONE:
				if(!player.isMounted())
					return true;
				break;
		}
		return false;
	}
}
